error id: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/config/DataSeedConfig.java:com/polleriacaporal/model/Usuario#
file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/config/DataSeedConfig.java
empty definition using pc, found symbol in pc: com/polleriacaporal/model/Usuario#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 118
uri: file:///C:/Users/USER/Desktop/Polleria_Caporal/PolleriaCaporal_V2/src/main/java/com/polleriacaporal/config/DataSeedConfig.java
text:
```scala
package com.polleriacaporal.config;

import com.polleriacaporal.model.RolUsuario;
import com.polleriacaporal.model.@@Usuario;
import com.polleriacaporal.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataSeedConfig {

    @Bean
    @Transactional
    CommandLineRunner seedUsuariosDemo(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Administrador");
            admin.setRol(RolUsuario.ROLE_ADMIN);
            admin.setEstado(true);

            Usuario employee = new Usuario();
            employee.setUsername("empleado");
            employee.setPassword(passwordEncoder.encode("emp123"));
            employee.setNombreCompleto("Mesero prototipo");
            employee.setRol(RolUsuario.ROLE_EMPLOYEE);
            employee.setEstado(true);

            usuarioRepository.save(admin);
            usuarioRepository.save(employee);
        };
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/polleriacaporal/model/Usuario#