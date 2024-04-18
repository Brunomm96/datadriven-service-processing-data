package br.com.datawake.datadrivenserviceprocessingdata.domain.service;

import br.com.datawake.datadrivenserviceprocessingdata.domain.model.ConfigGoalProduct;
import br.com.datawake.datadrivenserviceprocessingdata.domain.repository.ConfigGoalProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigGoalProductService {

    @Autowired
    ConfigGoalProductRepository configGoalProductRepository;

    public List<ConfigGoalProduct> listProductsToCalcMeta() {
        return configGoalProductRepository.findAll();
    }
}
