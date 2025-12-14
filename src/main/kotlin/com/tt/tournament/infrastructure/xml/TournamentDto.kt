package com.tt.tournament.infrastructure.xml

import jakarta.xml.bind.annotation.*

@XmlRootElement(name = "tournament")
@XmlAccessorType(XmlAccessType.FIELD)
data class TournamentDto(
    @field:XmlAttribute(name = "name")
    var name: String = "",

    @field:XmlAttribute(name = "start-date")
    var startDate: String = "",

    @field:XmlAttribute(name = "end-date")
    var endDate: String = "",

    @field:XmlElement(name = "competition")
    var competitions: MutableList<CompetitionDto> = mutableListOf()
)

@XmlAccessorType(XmlAccessType.FIELD)
data class CompetitionDto(
    @field:XmlAttribute(name = "age-group")
    var ageGroup: String = "",

    @field:XmlAttribute(name = "type")
    var type: String = "",

    @field:XmlAttribute(name = "entry-fee")
    var entryFee: String = "0.0",

    @field:XmlAttribute(name = "start-date")
    var startDate: String = "",

    @field:XmlAttribute(name = "sex")
    var sex: String = "",

    @field:XmlAttribute(name = "ttr-from")
    var ttrFrom: String? = null,

    @field:XmlAttribute(name = "ttr-to")
    var ttrTo: String? = null,

    @field:XmlAttribute(name = "ttr-remarks")
    var ttrRemarks: String? = null,

    @field:XmlElement(name = "players")
    var players: PlayersContainerDto? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class PlayersContainerDto(
    @field:XmlElement(name = "player")
    var players: MutableList<PlayerXmlDto> = mutableListOf()
)

@XmlAccessorType(XmlAccessType.FIELD)
data class PlayerXmlDto(
    @field:XmlAttribute(name = "id")
    var id: String = "",

    @field:XmlAttribute(name = "type")
    var type: String = "",

    @field:XmlElement(name = "person")
    var person: PersonDto? = null
)

@XmlAccessorType(XmlAccessType.FIELD)
data class PersonDto(
    @field:XmlAttribute(name = "licence-nr")
    var licenceNr: String = "",

    @field:XmlAttribute(name = "firstname")
    var firstname: String = "",

    @field:XmlAttribute(name = "lastname")
    var lastname: String = "",

    @field:XmlAttribute(name = "club-name")
    var clubName: String = "",

    @field:XmlAttribute(name = "club-nr")
    var clubNr: String = "",

    @field:XmlAttribute(name = "club-federation-nickname")
    var clubFederationNickname: String = "",

    @field:XmlAttribute(name = "birthyear")
    var birthyear: String? = null,

    @field:XmlAttribute(name = "sex")
    var sex: String = "",

    @field:XmlAttribute(name = "nationality")
    var nationality: String = "",

    @field:XmlAttribute(name = "ttr")
    var ttr: String? = null,

    @field:XmlAttribute(name = "internal-nr")
    var internalNr: String = "",

    @field:XmlAttribute(name = "region")
    var region: String = "",

    @field:XmlAttribute(name = "sub-region")
    var subRegion: String = ""
)
