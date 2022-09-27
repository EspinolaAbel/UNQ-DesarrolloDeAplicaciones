package ar.edu.unq.desapp.grupoN.desapp.persistence.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name="AppUser")
class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,
        var name: String,
        var lastName: String,
        var email: String,
        var address: String,
        var password: String,
        var cvu: String,
        var walletAddress: String,
    ) {

    constructor() : this(null, "", "", "", "", "","", "") {
    }
}
