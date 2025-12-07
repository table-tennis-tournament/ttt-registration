package com.tt.tournament.infrastructure.security

import com.tt.tournament.infrastructure.db.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        val authorities = user.authorities.map {
            SimpleGrantedAuthority(it.authority)
        }

        return User.builder()
            .username(user.username)
            .password(user.password)
            .authorities(authorities)
            .disabled(!user.enabled)
            .build()
    }
}
