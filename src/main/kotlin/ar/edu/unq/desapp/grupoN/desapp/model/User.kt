package ar.edu.unq.desapp.grupoN.desapp.model

import javax.persistence.*

@Entity(name="AppUser")
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var name: String,
        var lastName: String,
        @Column(unique = true)
        var email: String,
        var address: String,
        var password: String,
        var cvu: String,
        var walletAddress: String,
        var points: Int = 0,
        var closedOperations: Int = 0,
        var reputation: Int = 0
    ) {

}
