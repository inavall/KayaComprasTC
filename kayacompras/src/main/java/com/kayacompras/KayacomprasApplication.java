package com.kayacompras;

import com.kayacompras.model.Product;
import com.kayacompras.model.User;
import com.kayacompras.repository.ProductRepository;
import com.kayacompras.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@SpringBootApplication
public class KayacomprasApplication {

	public static void main(String[] args) {
		SpringApplication.run(KayacomprasApplication.class, args);
	}

	/**
	 * Inicializa alguns dados de exemplo ao iniciar a aplicação.
	 * Cria um usuário administrador e alguns produtos para teste.
	 */
	@Bean
	CommandLineRunner initData(UserRepository userRepository,
			ProductRepository productRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			// Criar usuário administrador se não existir
			if (!userRepository.existsByUsername("admin")) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setEmail("admin@kayacompras.co.mz");
				admin.setRole(User.Role.ADMIN);
				userRepository.save(admin);
				System.out.println("Usuário admin criado com sucesso!");
			}

			// Criar usuário cliente se não existir
			if (!userRepository.existsByUsername("cliente")) {
				User cliente = new User();
				cliente.setUsername("cliente");
				cliente.setPassword(passwordEncoder.encode("cliente"));
				cliente.setEmail("cliente@exemplo.co.mz");
				cliente.setRole(User.Role.CUSTOMER);
				userRepository.save(cliente);
				System.out.println("Usuário cliente criado com sucesso!");
			}

			// Adicionar alguns produtos de exemplo se não existirem
			if (productRepository.count() == 0) {
				// Produto 1
				Product product1 = new Product();
				product1.setName("Capulana Tradicional");
				product1.setDescription(
						"Capulana colorida tradicional de Sofala, perfeita para diversas ocasiões e usos.");
				product1.setPrice(new BigDecimal("750.00"));
				product1.setImageUrl("https://via.placeholder.com/350x200");
				product1.setStockQuantity(50);
				productRepository.save(product1);

				// Produto 2
				Product product2 = new Product();
				product2.setName("Cesto Artesanal");
				product2.setDescription(
						"Cesto artesanal feito à mão por artesãos locais de Marromeu, ideal para decoração e uso diário.");
				product2.setPrice(new BigDecimal("450.00"));
				product2.setImageUrl("https://via.placeholder.com/350x200");
				product2.setStockQuantity(30);
				productRepository.save(product2);

				// Produto 3
				Product product3 = new Product();
				product3.setName("Estatueta em Madeira");
				product3.setDescription("Estatueta decorativa esculpida em madeira de lei por artistas de Sofala.");
				product3.setPrice(new BigDecimal("1200.00"));
				product3.setImageUrl("https://via.placeholder.com/350x200");
				product3.setStockQuantity(15);
				productRepository.save(product3);

				// Produto 4
				Product product4 = new Product();
				product4.setName("Café de Marromeu");
				product4.setDescription(
						"Grãos de café premium cultivados nas terras férteis de Marromeu, pacote de 250g.");
				product4.setPrice(new BigDecimal("180.00"));
				product4.setImageUrl("https://via.placeholder.com/350x200");
				product4.setStockQuantity(100);
				productRepository.save(product4);

				// Produto 5
				Product product5 = new Product();
				product5.setName("Mel Puro de Sofala");
				product5.setDescription("Mel puro e natural produzido por apicultores locais de Sofala, pote de 500g.");
				product5.setPrice(new BigDecimal("220.00"));
				product5.setImageUrl("https://via.placeholder.com/350x200");
				product5.setStockQuantity(40);
				productRepository.save(product5);

				System.out.println("Produtos de exemplo adicionados com sucesso!");
			}
		};
	}
}
