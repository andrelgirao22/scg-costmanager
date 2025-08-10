package br.com.alg.scg.infra.config;

import br.com.alg.scg.domain.finance.service.SalePriceCalculatorService;
import br.com.alg.scg.domain.product.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

    @Bean
    public SalePriceCalculatorService salePriceCalculatorService(ProductRepository productRepository) {
        return new SalePriceCalculatorService(productRepository);
    }
}