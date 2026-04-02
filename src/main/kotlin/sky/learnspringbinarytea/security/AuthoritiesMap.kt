package sky.learnspringbinarytea.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.provisioning.JdbcUserDetailsManager
import java.util.Locale.getDefault
import javax.sql.DataSource

class RoleBasedJdbcUserDetailsManager(
    private val dataSource: DataSource
) : JdbcUserDetailsManager(dataSource) {
    private val roleAuthoritiesMap = mutableMapOf(
        "ROLE_USER" to AuthorityUtils.createAuthorityList(
            UserAuthorities.READ_MENU.name,
            UserAuthorities.READ_ORDER.name
        ),
        "ROLE_TEA_MAKER" to AuthorityUtils.createAuthorityList(
            UserAuthorities.READ_MENU.name, UserAuthorities.READ_ORDER.name,
            UserAuthorities.WRITE_ORDER.name
        ),
        "ROLE_MANAGER" to AuthorityUtils.createAuthorityList(*UserAuthorities.entries.map { it.name }.toTypedArray())

    )

    override fun addCustomAuthorities(username: String, authorities: MutableList<GrantedAuthority>) {
        val existing = authorities.mapNotNull { it.authority }.toMutableSet()

        authorities
            .asSequence()
            .mapNotNull { it.authority }
            .filter { it.uppercase(getDefault()).startsWith("ROLE_") }
            .forEach { role ->
                roleAuthoritiesMap[role]?.forEach { ga ->
                    ga.authority?.let {
                        if (existing.add(it)) {
                            authorities.add(ga)
                        }
                    }
                }
            }

        super.addCustomAuthorities(username, authorities)
    }
}
