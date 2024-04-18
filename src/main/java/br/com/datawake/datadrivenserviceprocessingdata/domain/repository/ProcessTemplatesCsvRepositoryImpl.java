package br.com.datawake.datadrivenserviceprocessingdata.domain.repository;

import br.com.datawake.datadrivenserviceprocessingdata.domain.exception.UploadsCsvNotReadException;
import br.com.datawake.datadrivenserviceprocessingdata.domain.model.*;
import br.com.datawake.datadrivenserviceprocessingdata.domain.service.*;
import br.com.datawake.datadrivenserviceprocessingdata.domain.utils.DatadrivenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Repository
public class ProcessTemplatesCsvRepositoryImpl implements ProcessTemplatesCsvRepository {

    private boolean isStaticTable = false;
    private List<TableStructModel> structTable;
    private String[] headerFile;
    private String fieldKeyTable;
    private DwTableModel insertControl;
    private List<FieldContentModel> whereInsertControl;
    private Map<String, String> lineInsertMapStatic = new HashMap<>();
    private Map<String, AttributeValuesCsv> lineInsertMapDynamic = new HashMap<>();
    private Map<String, Object> KeyLine = new HashMap<>();
    private Integer idUserProcess = 1;
    private Long lineCount;
    private String lineBuffer;
    private UploadsCsvTemplate uploadFile;
    private Long technicalSheetId;
    private Long ncountLog;
    private List<TechnicalSheetKey> keyTechnicalSheet;
    private String recordKey;
    private Boolean allFieldsTrue;
    private OffsetDateTime dateTimeProcess;


    @Value("${app.path.uploads.csv.templates}")
    private String pathArquivos;

    @Autowired
    private StructCsvTemplateService structCsvTemplateService;

    @Autowired
    private IdTableDwService idTableDwService;

    @Autowired
    private UploadsCsvService uploadsCsvService;

    @Autowired
    private RelationsOfForeignKeyRepository relationsOfForeignKeyRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private UploadsCsvLogService uploadsCsvLogService;

    @Autowired
    private TechnicalSheetCustomRepository technicalSheetCustomRepository;

    @Autowired
    private TechnicalSheetKeyService technicalSheetKeyService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Override
    public void execTemplatesPending( String db ){
        List<UploadsCsvTemplate> templatesCsvPendentes = uploadsCsvService.findByStatusFile( UploadCsvStatus.PENDING );
        process( templatesCsvPendentes );
    }

    public void process(List<UploadsCsvTemplate> templatesCsvPendentes ) {

        Path diretorioPath = Paths.get(pathArquivos);
        Path arquivoPath;

        for( UploadsCsvTemplate uploadsCsvTemplate : templatesCsvPendentes){

            uploadFile = uploadsCsvTemplate;

            // Atualiza status para em processamento
            uploadFile = uploadsCsvService.changeStatusFile(uploadFile , UploadCsvStatus.PROCESSING );

            arquivoPath = diretorioPath.resolve( uploadFile.getNameFile() );

            dateTimeProcess = OffsetDateTime.now(ZoneOffset.UTC);

            propertiesTable( );
            readFile( arquivoPath  );
            if( uploadFile.getStatusFile().equals(UploadCsvStatus.PROCESSING ) ){
                uploadFile = uploadsCsvService.changeStatusFile(uploadFile , UploadCsvStatus.SUCESS );
                try {
                    Files.delete(arquivoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void propertiesTable( ){

        structTable = structCsvTemplateService.structCsv( uploadFile.getTemplatesCsv() );
        String table;

        isStaticTable = uploadFile.getTemplatesCsv().isStaticTable();
        if (isStaticTable){
            table = uploadFile.getTemplatesCsv().getDwTable();

            fieldKeyTable = relationsOfForeignKeyRepository.getDwTableKey(table);

            insertControl = new DwTableModel( table );

        } else {
            technicalSheetId = uploadFile.getTemplatesCsv().getTechnicalSheet().getId();
            insertControl = new DwTableModel( "dw_attribute" );

            Optional<List<TechnicalSheetKey>> keyTechnicalSheetOp = technicalSheetKeyService.findByTechnicalSheet( uploadFile.getTemplatesCsv().getTechnicalSheet() );
            if (keyTechnicalSheetOp.isPresent()){
                // Campos chave do formulario
                keyTechnicalSheet = keyTechnicalSheetOp.get();
            }

        }

    }

    private void readFile( Path arquivoPath ) {

        Charset charset = Charset.forName("UTF-8");

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoPath.toString(), charset ))) {
            lineCount = 0L;

            lineCount++;
            lineBuffer = br.readLine(); // cabeçalho
            headerFile = lineBuffer.split(";");

            if ( validStructFile() ){

                lineCount++;
                lineBuffer = br.readLine();// segunda linha - com conteudo

                while (lineBuffer != null && !lineBuffer.isEmpty() ) {

                    // Limpa o buffer das variaveis de controle da leitura da linha
                    whereInsertControl = new ArrayList<FieldContentModel>();
                    lineInsertMapStatic = new HashMap<>();
                    lineInsertMapDynamic = new HashMap<>();
                    KeyLine = new HashMap<>();
                    ncountLog = 0L;

                    if ( isStaticTable  ){
                        importTableStatic();

                    }else  {
                        // insert somente na atribute dw_attribute_values
                        importTableDynamic();
                    }

                    lineCount++;
                    lineBuffer = br.readLine();
                }

            }

        }
        catch (IOException e) {
            throw new UploadsCsvNotReadException( e.getMessage() );

        }

    }

    private void importTableStatic(){
        if ( validLineStatic() ){
            StringBuilder sb = queryTableStatic();
            insertTable( sb );
        }

    }

    private boolean validLineStatic(){
        String[] vect = lineBuffer.split(";");

        boolean lcontinue=true;
        Integer lenghtVar;
        Integer hasId;
        StringBuilder msg = new StringBuilder();

        if ( ( headerFile.length == vect.length ) && ( headerFile.length  == structTable.size()  ) ){
            for( int i = 0 ; i < vect.length; i++ ){

                //Nome da coluna
                String column = headerFile[i];

                // Verifica se a coluna deve ser utilizada no where para localizar se já existe o registro na tabela
                if (isStaticTable && fieldKeyTable.contains(column)){
                    whereInsertControl.add(  new FieldContentModel( column , vect[i]) );
                    insertControl.setWhereDw(whereInsertControl);
                }

                //Verifica se o nome da coluna do CSV existe na Estrutura da tabela
                hasId = structTable.indexOf(structTable.stream().filter( x -> x.getNameColumn().equals(column) )
                        .findFirst()
                        .orElse(null));

                if (hasId >= 0){

                    // Verifica se o campo, é referente a uma chave estrangeria, para buscar o Id da tabela estrangeira
                    if ( structTable.get(hasId).getReferenceKeyTable() != null &&
                            !structTable.get(hasId).getReferenceKeyTable().isEmpty() ){


                        String idTable = foreignKeyId( structTable.get(hasId).getReferenceTable(),
                                structTable.get(hasId).getReferenceKeyTable(),
                                vect[i]  );

                        if (idTable == null || idTable.isEmpty() || idTable.equals("0")){

                            lcontinue = false;
                            msg.append( messageLog(String.format("Não foi localizado o cadastro (id) na tabela [%s]. "+
                                            "Verifique a coluna [%s] do arquivo csv. "+
                                            "Com conteúdo dessa coluna é realizada a consulta na tabela [%s] filtrando o campo [%s],"+
                                            "o conteúdo dessa coluna deve existir nessa tabela relacionada. O conteúdo atual [%s] não existe.\n" ,
                                    structTable.get(hasId).getReferenceTable() ,
                                    column ,
                                    structTable.get(hasId).getReferenceTable() ,
                                    structTable.get(hasId).getReferenceKeyTable(),
                                    vect[i] )));


                        }else{
                            vect[i] = idTable; // Atualiza a coluna, com o Id da tabela estrangeira
                        }

                    }

                    // Campos Varchar, valida o tamanho da campo
                    if ( structTable.get(hasId).getTypeColumn().equals("VARCHAR") || structTable.get(hasId).getTypeColumn().equals("STRING") ) {
                        lenghtVar =  structTable.get(hasId).getLengthColumn() ;

                        if( vect[i].length() > lenghtVar ){
                            lcontinue = false;
                            msg.append( messageLog( String.format("A quantidade de caracteres no campo [%s] é superior ao tamanho máximo(%d) permitido na tabela.\n" ,
                                    column,
                                    lenghtVar ) ) );
                        }
                    }

                    lineInsertMapStatic.put( column , vect[i] );

                }

            }
        }else{

            lcontinue = false;
            msg.append( messageLog( "Numero de colunas do arquivo menor do que a quantidade de colunas default do template de CSV dessa tabela." +
                    "Realize novamente o download e NÃO altere os nomes e sequência das colunas\n" ));

        }

        if ( !lcontinue ){
            saveLog( msg.toString() );
        }

        return lcontinue;
    }

    private String foreignKeyId(String table, String field , String content){
        List<FieldContentModel> oWhere = new ArrayList<FieldContentModel>();
        oWhere.add( new FieldContentModel( field , content ) );

        DwTableModel getIdTable = new DwTableModel( table , oWhere  );

        return idTableDwService.getIdTableDw( getIdTable );
    }


    private StringBuilder queryTableStatic(){
        StringBuilder sb = new StringBuilder();
        StringBuilder contents = new StringBuilder();
        StringBuilder fields = new StringBuilder();
        StringBuilder joins = new StringBuilder();
        StringBuilder cabec = new StringBuilder();
        StringBuilder values = new StringBuilder();
        int i = 0;

        sb.append(" MERGE [dbo].["+insertControl.getTableDw()+"] AS Destino ");

        //Regra do Compare
        for ( FieldContentModel where : insertControl.getWhereDw() ){
            i++;
            if (i>1){
                contents.append(" , '");
                fields.append(", ");
                joins.append(" AND ");
            }
            contents.append("'"+ where.getContent()+"'");
            fields.append(""+ where.getField()+"");
            joins.append(" Destino."+ where.getField() + " = src."+where.getField()+" ");
        }
        sb.append(" USING (SELECT  "+ contents+" ) as src ("+ fields +" ) ");
        sb.append("    ON ( "+joins+" ) ");

        sb.append(" WHEN MATCHED THEN ");
        sb.append("    UPDATE ");
        sb.append("    SET ");

        //Regra para o UPDATE
        lineInsertMapStatic.forEach( (fied, content)->{
            if ( !cabec.isEmpty() ){
                cabec.append(  "," );
                values.append( "," );
            }
            sb.append("        DESTINO."+fied+"	 = '"+ content+"'	,");
            cabec.append( fied ) ;
            values.append( "'" +content + "'" );
        });
        sb.append("    DESTINO.user_id_update   = '"+ idUserProcess +"',");
        sb.append("    DESTINO.date_update	    = '" + dateTimeProcess + "' ");

        sb.append(" WHEN NOT MATCHED THEN ");
        //Regra para o Insert
        sb.append("    insert ");
        sb.append("( ");
        sb.append( cabec.toString() );
        sb.append(",user_id_insert");
        sb.append(",date_insert");
        sb.append(",user_id_update");
        sb.append(",date_update");
        sb.append(",access_key");
        sb.append(")");
        sb.append("VALUES");
        sb.append("( ");
        sb.append( values.toString() );
        sb.append(",'"+idUserProcess+"' ");
        sb.append(",'" + dateTimeProcess + "' " );
        sb.append(",NULL");
        sb.append(",NULL");
        sb.append(",NEWID()");
        sb.append(")");
        sb.append(";");

        return sb;
    }

    private void importTableDynamic(){
        if ( validLineDynamic() ) {
            StringBuilder sb = queryTableDynamic() ;
            insertTable( sb );
        }

    }

    private boolean validLineDynamic(){
        String[] vect = lineBuffer.split(";");

        boolean lcontinue=true;
        Integer lenghtVar;
        Integer hasId;
        Integer nKey;
        StringBuilder msg = new StringBuilder();
        String idTable;
        String[] listOptions;
        String content;
        String keyOptions;

        if ( ( headerFile.length == vect.length ) && ( headerFile.length  == structTable.size()  ) ){
            for( int i = 0 ; i < vect.length; i++ ){

                //Nome da coluna no arquivo
                String column = headerFile[i];

                // Verifica se é um campo que faz parte da Key do Registro
                nKey = keyTechnicalSheet.indexOf(keyTechnicalSheet.stream().filter( x -> x.getAttribute().getAttribute().equals(column) )
                        .findFirst()
                        .orElse(null));
                if (nKey>=0){
                    KeyLine.put( column , vect[i] );
                }

                //Verifica se o nome da coluna do CSV existe na Estrutura da tabela
                hasId = structTable.indexOf(structTable.stream().filter( x -> x.getNameColumn().equals(column) )
                        .findFirst()
                        .orElse(null));

                if (hasId >= 0){

                    String typeColumn = structTable.get(hasId).getTypeColumn();

                    idTable = foreignKeyId( "dw_attribute",
                            "attribute",
                            column  );

                    if (idTable == null || idTable.isEmpty() || idTable.equals("0")){

                        lcontinue = false;
                        msg.append( messageLog( String.format("Não foi localizado o atributo [%s] na tabela dw_attribute. "+
                                        "Verifique o nome da coluna e o cadastro de atributos.\n" ,
                                column ) ) );

                    }

                    // Campos Varchar, valida o tamanho da campo
                    if ( typeColumn.equals("VARCHAR") || typeColumn.equals("STRING") ) {
                        lenghtVar =  structTable.get(hasId).getLengthColumn() ;

                        if( vect[i].length() > lenghtVar ){
                            lcontinue = false;
                            msg.append( messageLog(String.format("A quantidade de caracteres no campo [%s] é superior ao tamanho máximo(%d) permitido na tabela.\n" ,
                                    column,
                                    lenghtVar ) ) );
                        }
                    }

                    if ( typeColumn.equals("LIST") ) {

                        listOptions = structTable.get(hasId).getListOptions().split(";");
                        keyOptions = "";
                        if ( listOptions.length > 0){

                            for(int y = 0 ; y < listOptions.length; y++ ){

                                content = listOptions[y].substring(listOptions[y].indexOf("=")+1);
                                if( content.toUpperCase().equals(vect[i].toUpperCase()) ){
                                    keyOptions = listOptions[y].substring(0,listOptions[y].indexOf("="));
                                    break;
                                }
                            }

                            if( !keyOptions.isEmpty()){
                                vect[i] = keyOptions;
                            } else {
                                lcontinue = false;
                                msg.append( messageLog(String.format("A opção da lista do campo [%s] invalida. Verifique a lista de opções para este campo, informe o conteudo após o sinal de =  [%s] \n" ,
                                        column,
                                        structTable.get(hasId).getListOptions() ) ) );
                            }

                        }

                    }

                    if (lcontinue){

                        AttributeValuesCsv attributeValues = new AttributeValuesCsv();
                        if (typeColumn.equals("VARCHAR") || typeColumn.equals("STRING")){
                            attributeValues.setValueString( "'" + vect[i] + "'" );

                        }else if (typeColumn.equals("NUMBER")){
                            vect[i] = vect[i].replace(",",".");
                            attributeValues.setValueNumber( Double.valueOf(vect[i]) );

                        }else if (typeColumn.equals("DATE")){
                            attributeValues.setValueDate(  "'" + vect[i] + "'" );

                        }else if (typeColumn.equals("BOOLEAN") ){
                            attributeValues.setValueBoolean(  vect[i].equals("0") ? 0 : 1 );

                        }else if (typeColumn.equals("LIST")){
                            attributeValues.setValueList( "'" + vect[i] + "'" );
                        }

                        lineInsertMapDynamic.put( idTable , attributeValues);

                    }

                }

            }

        }else{
            lcontinue = false;
            msg.append( messageLog( "Numero de colunas do arquivo menor do que a quantidade de colunas default do template de CSV dessa tabela." +
                    "Realize novamente o download e NÃO altere os nomes e sequência das colunas\n" ) );

        }

        if ( !lcontinue ){
            saveLog( msg.toString() );

        }

        return lcontinue;
    }

    private StringBuilder queryTableDynamic(){

        StringBuilder sb = new StringBuilder();
        String recordAccessKey = getAccessKeyTechnicalSheet();

        lineInsertMapDynamic.forEach( (idTable, attributes )->{

            sb.append(" MERGE [dbo].[dw_attribute_values] AS Destino ");
            sb.append(" USING (SELECT  '"+ recordAccessKey+"' , "+ idTable +" ) as src ");
            sb.append(" ( record_access_key , attribute_id ) ");
            sb.append(" ON ( Destino.record_access_key = src.record_access_key ");
            sb.append("         AND Destino.attribute_id = src.attribute_id  ) ");

            sb.append(" WHEN MATCHED THEN ");
            sb.append("    UPDATE ");
            sb.append("    SET ");
            sb.append("    DESTINO.value_string    = "+attributes.getValueString()+"");
            sb.append("    ,DESTINO.value_number    = "+attributes.getValueNumber()+"");
            sb.append("    ,DESTINO.value_boolean   = "+attributes.getValueBoolean()+"");
            sb.append("    ,DESTINO.value_date      = "+attributes.getValueDate()+"");
            sb.append("    ,DESTINO.value_list      = "+attributes.getValueList()+"");
            sb.append("    ,DESTINO.value_multiple  = "+attributes.getValueMultiple()+"");
            sb.append("    ,DESTINO.user_id_update  = '"+ idUserProcess +"'");
            sb.append("    ,DESTINO.date_update	   = '" + dateTimeProcess + "' ");

            sb.append(" WHEN NOT MATCHED THEN ");
            //Regra para o Insert
            sb.append("    insert ");
            sb.append("(technical_sheet_id ");
            sb.append(",attribute_id ");
            sb.append(",value_string ");
            sb.append(",value_number ");
            sb.append(",value_boolean ");
            sb.append(",value_date ");
            sb.append(",value_list ");
            sb.append(",value_multiple ");
            sb.append(",record_access_key ");
            sb.append(",user_id_insert ");
            sb.append(",date_insert ");
            sb.append(",user_id_update ");
            sb.append(",date_update ");
            sb.append(",access_key) ");
            sb.append("VALUES (");
            sb.append(""+technicalSheetId+"");
            sb.append(","+idTable+"");
            sb.append(","+attributes.getValueString()+"");
            sb.append(","+attributes.getValueNumber()+"");
            sb.append(","+attributes.getValueBoolean()+"");
            sb.append(","+attributes.getValueDate()+"");
            sb.append(","+attributes.getValueList()+"");
            sb.append(","+attributes.getValueMultiple()+"");
            sb.append(",'"+ recordAccessKey +"'");
            sb.append(",'"+idUserProcess+"' ");
            sb.append(",'" + dateTimeProcess + "' ");
            sb.append(",NULL");
            sb.append(",NULL");
            sb.append(",NEWID()");
            sb.append(")");
            sb.append(";");

        });

        return sb;
    }

    private void insertTable( StringBuilder sb ){
        try {
            transactionTemplate = new TransactionTemplate(transactionManager);

            transactionTemplate.execute(transactionStatus -> {
                entityManager.createNativeQuery(sb.toString()).executeUpdate();
                transactionStatus.flush();
                return null;
            });


        } catch (Exception e) {
            saveLog( String.format("Erro na execução do merge: %s - comando merge: %s",  e.getMessage() , sb.toString() )  );

        }

    }

    private void saveLog( String messageLine ){
        // Marca o arquivo como processado parcialmente
        uploadFile = uploadsCsvService.changeStatusFile(uploadFile , UploadCsvStatus.WARNS );

        if (messageLine.length() > 8000 ){
            messageLine = messageLine.substring(0,8000) ;
        }

        if (lineBuffer.length() > 8000 ){
            lineBuffer = lineBuffer.substring(0,8000) ;
        }

        UploadsCsvTemplateLog log = new UploadsCsvTemplateLog();
        log.setUploadsCsvTemplate( uploadFile );
        log.setLineFile( lineCount );
        log.setContentLine( lineBuffer );
        log.setMessageLine( messageLine );
        //log.setAccessKey(DatadrivenUtils.getNewAccessKey());//
        uploadsCsvLogService.save(log);

    }

    private String messageLog( String msg ){
        return "-> "+ ++ncountLog +" " + msg;
    }

    private String getAccessKeyTechnicalSheet(){

        List<Map<String, String>> attributeValuesExist = technicalSheetCustomRepository.technicalSheetByKeys( uploadFile.getTemplatesCsv().getTechnicalSheet() , KeyLine );

        recordKey = "";

        for( int i = 0 ; i <  attributeValuesExist.size(); i++){

            Map<String, String> lista = attributeValuesExist.get(i);
            if (existsKey( lista )){
                recordKey = lista.get("recordAccessKey").toString();
                break;
            }
        }
        if(recordKey.isEmpty()){
            recordKey = DatadrivenUtils.getNewAccessKey();
        }

        return recordKey;

    }

    private Boolean existsKey( Map<String, String> lista ){

        allFieldsTrue = true;
        KeyLine.forEach( ( column , field )->{
            if( !lista.get(column).equals(field) ){
                allFieldsTrue = false;
            }
        });

        return allFieldsTrue;

    }

    private Boolean validStructFile(){
        StringBuilder msg = new StringBuilder();
        Boolean lContinue = true;
        Boolean existField = false;
        Integer hasId;
        ncountLog = 0L;

        if (  headerFile.length  == structTable.size()  ){
            for( int i = 0 ; i < headerFile.length; i++ ){

                String column = headerFile[i];

                hasId = structTable.indexOf(structTable.stream().filter( x -> x.getNameColumn().equals(column) )
                        .findFirst()
                        .orElse(null));

                if( hasId < 0 ) {
                    lContinue = false;
                    msg.append( messageLog( String.format("Nome da coluna [%s] no CSV não localizadao na lista de atributos da tabela." +
                            "Realize novamente o download e NÃO altere os nomes e sequência das colunas\n" , column )));

                }
            }


            if( lContinue ){
                for( int x = 0 ; x < structTable.size(); x++){

                    String column = structTable.get(x).getNameColumn() ;
                    existField = false;

                    for( int i = 0 ; i < headerFile.length; i++ ){
                        if ( column.equals( headerFile[i] ) ){
                            existField = true;

                        }
                    }

                    if (!existField){
                        lContinue = false;
                        msg.append( messageLog( String.format("Não foi localizado o atributo [%s] da tabela no arquivo CSV." +
                                "Realize novamente o download e NÃO altere os nomes e sequência das colunas\n" , column )));
                    }



                }
            }

        } else {
            lContinue = false;
            msg.append( messageLog( "Numero de colunas do arquivo é diferente da quantidade de colunas default do template de CSV dessa tabela." +
                    "Realize novamente o download e NÃO altere os nomes e sequência das colunas\n" ));
        }

        if (!lContinue){
            saveLog( msg.toString() );
        }

        return lContinue;
    }

}
