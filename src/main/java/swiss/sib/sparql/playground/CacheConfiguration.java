package swiss.sib.sparql.playground;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Cache is enabled with the profile cache
 * @author Daniel Teixeira http://github.com/ddtxra
 */
@Configuration
@EnableCaching
@Profile("!nocache")
public class CacheConfiguration {


}