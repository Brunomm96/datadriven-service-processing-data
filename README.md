# Projeto de serviços para processamento de dados

## Serviços
O projeto é configurado para rodar alguns serviços de tempos em tempos. O Serviço é configurado na classe <b>ScheduledTasks</b>.<br> 
Fica no diretório <b>src/main/java/br/com/datawake/datadrivenserviceprocessingdata/domain/schedule/ScheduledTasks.java</b>

## Configuração Multiplos bancos
Este projeto foi configurado para ter acesso simultâneo a múltiplos banco de dados. A configuração de acesso aos bancos fica no diretório <b>allTenants</b>, na raiz do projeto

### Diretório para configuração de acesso a multiplos bancos

#### Nome dos arquivos
O nome do arquivo deve seguir um padrão, e sequencial. O nome deve iniciar com o texto <b>tenant_</b> seguido de um sequencial numérico. Terminando com a extensão <b>.properties</b>
<br><br>
Exemplo:
<br><br>
tenant_1.properties<br>
tenant_2.properties<br>
tenant_3.properties<br>
...

#### Formato do arquivo
name=<NAME_DATA_BASE><br>
datasource.url=<URL_JDBC><br>
datasource.username=<USER_DB><br>
datasource.password=<PASS_DB><br>
datasource.driver-class-name=<NAME_CLASS_DRIVER><br>

#### Exemplo de preenchimento
name=db_test<br>
datasource.url=jdbc:sqlserver://localhost:1433;databaseName=db_test;encrypt=true;trustServerCertificate=true;<br>
datasource.username=dbname<br>
datasource.password=secret<br>
datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver<br>