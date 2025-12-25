package com.tt.tournament.xmlimport.application

import com.tt.tournament.infrastructure.db.H2TestDatabase
import com.tt.tournament.xmlimport.infrastructure.database.ClubRepository
import com.tt.tournament.xmlimport.infrastructure.database.PlayerRepository
import com.tt.tournament.xmlimport.infrastructure.database.TypeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Import(H2TestDatabase::class)
@Transactional
class XmlImportDeletionTest(
    @Autowired private val xmlImportService: XmlImportService,
    @Autowired private val playerRepository: PlayerRepository,
    @Autowired private val clubRepository: ClubRepository,
    @Autowired private val typeRepository: TypeRepository
) {

    @BeforeEach
    fun setup() {
        // Clean up any existing test data
        // Note: H2TestDatabase handles schema initialization
    }

    @Test
    fun `Given existing players in competition when importing XML with fewer players then deleted players are removed from typeperplayer`() {
        // given - Set up initial data in database
        val clubId = clubRepository.create("Test Club", "BaTTV", "123")
        val club = clubRepository.findByName("Test Club")!!

        // Create 3 players
        val player1Id = playerRepository.createPlayer(
            firstName = "Thomas",
            lastName = "Fritsche",
            licenseNr = "1080",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1974"
        )

        val player2Id = playerRepository.createPlayer(
            firstName = "Peter",
            lastName = "Mayer",
            licenseNr = "9983",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1954"
        )

        val player3Id = playerRepository.createPlayer(
            firstName = "Nicole",
            lastName = "Schmidt",
            licenseNr = "60415",
            clubId = club.id,
            sex = "w",
            nationality = "DE",
            ttr = 1509.0,
            birthYear = "1975"
        )

        // Create competition type
        val typeId = typeRepository.create("Senioren 40 Einzel", 13.0)

        // Enroll all 3 players in the competition
        playerRepository.enrollPlayerInType(player1Id, typeId, 0)
        playerRepository.enrollPlayerInType(player2Id, typeId, 0)
        playerRepository.enrollPlayerInType(player3Id, typeId, 0)

        // Verify all 3 are enrolled
        assertThat(playerRepository.isPlayerEnrolledInType(player1Id, typeId)).isTrue()
        assertThat(playerRepository.isPlayerEnrolledInType(player2Id, typeId)).isTrue()
        assertThat(playerRepository.isPlayerEnrolledInType(player3Id, typeId)).isTrue()

        // when - Import XML that only contains 2 of the 3 players (player2 is missing)
        val xmlContent = """
            <?xml version="1.0" encoding="utf-8"?>
            <tournament name="Test Tournament" start-date="2026-01-03" end-date="2026-01-03">
                <competition age-group="Senioren 40" type="Einzel" entry-fee="13.0" start-date="2026-01-03 11:30" sex="gemischt">
                    <players>
                        <player type="single" id="PLAYER1">
                            <person licence-nr="1080" firstname="Thomas" lastname="Fritsche"
                                    club-name="Test Club" club-nr="123" club-federation-nickname="BaTTV"
                                    sex="1" nationality="DE" birthyear="1974"/>
                        </player>
                        <player type="single" id="PLAYER3">
                            <person licence-nr="60415" firstname="Nicole" lastname="Schmidt"
                                    club-name="Test Club" club-nr="123" club-federation-nickname="BaTTV"
                                    sex="0" nationality="DE" birthyear="1975" ttr="1509"/>
                        </player>
                    </players>
                </competition>
            </tournament>
        """.trimIndent()

        val result = xmlImportService.importTournamentData(xmlContent)

        // then
        assertThat(result.success).isTrue()
        assertThat(result.summary).isNotNull
        assertThat(result.summary!!.enrollmentsDeleted).isEqualTo(1) // player2 (Mayer) should be deleted

        // Verify player1 and player3 are still enrolled
        assertThat(playerRepository.isPlayerEnrolledInType(player1Id, typeId)).isTrue()
        assertThat(playerRepository.isPlayerEnrolledInType(player3Id, typeId)).isTrue()

        // Verify player2 is no longer enrolled
        assertThat(playerRepository.isPlayerEnrolledInType(player2Id, typeId)).isFalse()

        // Verify all players still exist in player table
        assertThat(playerRepository.findByLicenseNr("1080")).isNotNull
        assertThat(playerRepository.findByLicenseNr("9983")).isNotNull
        assertThat(playerRepository.findByLicenseNr("60415")).isNotNull
    }

    @Test
    fun `Given existing players when importing XML with empty player list then all players are removed from that competition`() {
        // given - Set up initial data
        val clubId = clubRepository.create("Test Club", "BaTTV", "123")
        val club = clubRepository.findByName("Test Club")!!

        val player1Id = playerRepository.createPlayer(
            firstName = "Thomas",
            lastName = "Fritsche",
            licenseNr = "1080",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1974"
        )

        val player2Id = playerRepository.createPlayer(
            firstName = "Peter",
            lastName = "Mayer",
            licenseNr = "9983",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1954"
        )

        val typeId = typeRepository.create("Senioren 50 Einzel", 13.0)

        playerRepository.enrollPlayerInType(player1Id, typeId, 0)
        playerRepository.enrollPlayerInType(player2Id, typeId, 0)

        // when - Import XML with empty player list
        val xmlContent = """
            <?xml version="1.0" encoding="utf-8"?>
            <tournament name="Test Tournament" start-date="2026-01-03" end-date="2026-01-03">
                <competition age-group="Senioren 50" type="Einzel" entry-fee="13.0" start-date="2026-01-03 15:30" sex="gemischt">
                    <players>
                    </players>
                </competition>
            </tournament>
        """.trimIndent()

        val result = xmlImportService.importTournamentData(xmlContent)

        // then
        assertThat(result.success).isTrue()
        assertThat(result.summary!!.enrollmentsDeleted).isEqualTo(2) // Both players should be deleted

        assertThat(playerRepository.isPlayerEnrolledInType(player1Id, typeId)).isFalse()
        assertThat(playerRepository.isPlayerEnrolledInType(player2Id, typeId)).isFalse()

        // Players still exist in player table
        assertThat(playerRepository.findByLicenseNr("1080")).isNotNull
        assertThat(playerRepository.findByLicenseNr("9983")).isNotNull
    }

    @Test
    fun `Given existing players when importing XML with all existing players then no deletions occur`() {
        // given
        val clubId = clubRepository.create("Test Club", "BaTTV", "123")
        val club = clubRepository.findByName("Test Club")!!

        val player1Id = playerRepository.createPlayer(
            firstName = "Thomas",
            lastName = "Fritsche",
            licenseNr = "1080",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1974"
        )

        val typeId = typeRepository.create("Senioren 40 Einzel", 13.0)
        playerRepository.enrollPlayerInType(player1Id, typeId, 0)

        // when - Import XML with same player
        val xmlContent = """
            <?xml version="1.0" encoding="utf-8"?>
            <tournament name="Test Tournament" start-date="2026-01-03" end-date="2026-01-03">
                <competition age-group="Senioren 40" type="Einzel" entry-fee="13.0" start-date="2026-01-03 11:30" sex="gemischt">
                    <players>
                        <player type="single" id="PLAYER1">
                            <person licence-nr="1080" firstname="Thomas" lastname="Fritsche"
                                    club-name="Test Club" club-nr="123" club-federation-nickname="BaTTV"
                                    sex="1" nationality="DE" birthyear="1974"/>
                        </player>
                    </players>
                </competition>
            </tournament>
        """.trimIndent()

        val result = xmlImportService.importTournamentData(xmlContent)

        // then
        assertThat(result.success).isTrue()
        assertThat(result.summary!!.enrollmentsDeleted).isEqualTo(0) // No deletions

        assertThat(playerRepository.isPlayerEnrolledInType(player1Id, typeId)).isTrue()
    }

    @Test
    fun `Given existing players when importing XML with only new players then old players are deleted`() {
        // given
        val clubId = clubRepository.create("Test Club", "BaTTV", "123")
        val club = clubRepository.findByName("Test Club")!!

        // Create existing player
        val oldPlayerId = playerRepository.createPlayer(
            firstName = "Old",
            lastName = "Player",
            licenseNr = "99999",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1970"
        )

        val typeId = typeRepository.create("Senioren 40 Einzel", 13.0)
        playerRepository.enrollPlayerInType(oldPlayerId, typeId, 0)

        // when - Import XML with completely different player
        val xmlContent = """
            <?xml version="1.0" encoding="utf-8"?>
            <tournament name="Test Tournament" start-date="2026-01-03" end-date="2026-01-03">
                <competition age-group="Senioren 40" type="Einzel" entry-fee="13.0" start-date="2026-01-03 11:30" sex="gemischt">
                    <players>
                        <player type="single" id="PLAYER1">
                            <person licence-nr="1080" firstname="Thomas" lastname="Fritsche"
                                    club-name="Test Club" club-nr="123" club-federation-nickname="BaTTV"
                                    sex="1" nationality="DE" birthyear="1974"/>
                        </player>
                    </players>
                </competition>
            </tournament>
        """.trimIndent()

        val result = xmlImportService.importTournamentData(xmlContent)

        // then
        assertThat(result.success).isTrue()
        assertThat(result.summary!!.enrollmentsDeleted).isEqualTo(1) // Old player deleted
        assertThat(result.summary!!.enrollmentsCreated).isEqualTo(1) // New player added

        assertThat(playerRepository.isPlayerEnrolledInType(oldPlayerId, typeId)).isFalse()

        val newPlayer = playerRepository.findByLicenseNr("1080")
        assertThat(newPlayer).isNotNull
        assertThat(playerRepository.isPlayerEnrolledInType(newPlayer!!.id, typeId)).isTrue()
    }

    @Test
    fun `Given multiple competitions when importing then deletions are tracked separately per competition`() {
        // given
        val clubId = clubRepository.create("Test Club", "BaTTV", "123")
        val club = clubRepository.findByName("Test Club")!!

        // Create players
        val player1Id = playerRepository.createPlayer(
            firstName = "Player",
            lastName = "One",
            licenseNr = "1001",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1970"
        )

        val player2Id = playerRepository.createPlayer(
            firstName = "Player",
            lastName = "Two",
            licenseNr = "1002",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1971"
        )

        val player3Id = playerRepository.createPlayer(
            firstName = "Player",
            lastName = "Three",
            licenseNr = "1003",
            clubId = club.id,
            sex = "m",
            nationality = "DE",
            ttr = null,
            birthYear = "1972"
        )

        // Create two competitions
        val type1Id = typeRepository.create("Senioren 40 Einzel", 13.0)
        val type2Id = typeRepository.create("Senioren 50 Einzel", 13.0)

        // Enroll players: player1 and player2 in competition 1, player3 in competition 2
        playerRepository.enrollPlayerInType(player1Id, type1Id, 0)
        playerRepository.enrollPlayerInType(player2Id, type1Id, 0)
        playerRepository.enrollPlayerInType(player3Id, type2Id, 0)

        // when - Import XML that removes player2 from competition 1 and player3 from competition 2
        val xmlContent = """
            <?xml version="1.0" encoding="utf-8"?>
            <tournament name="Test Tournament" start-date="2026-01-03" end-date="2026-01-03">
                <competition age-group="Senioren 40" type="Einzel" entry-fee="13.0" start-date="2026-01-03 11:30" sex="gemischt">
                    <players>
                        <player type="single" id="PLAYER1">
                            <person licence-nr="1001" firstname="Player" lastname="One"
                                    club-name="Test Club" club-nr="123" club-federation-nickname="BaTTV"
                                    sex="1" nationality="DE" birthyear="1970"/>
                        </player>
                    </players>
                </competition>
                <competition age-group="Senioren 50" type="Einzel" entry-fee="13.0" start-date="2026-01-03 15:30" sex="gemischt">
                    <players>
                    </players>
                </competition>
            </tournament>
        """.trimIndent()

        val result = xmlImportService.importTournamentData(xmlContent)

        // then
        assertThat(result.success).isTrue()
        assertThat(result.summary!!.enrollmentsDeleted).isEqualTo(2) // player2 and player3 deleted

        // Competition 1: only player1 should remain
        assertThat(playerRepository.isPlayerEnrolledInType(player1Id, type1Id)).isTrue()
        assertThat(playerRepository.isPlayerEnrolledInType(player2Id, type1Id)).isFalse()

        // Competition 2: no players should remain
        assertThat(playerRepository.isPlayerEnrolledInType(player3Id, type2Id)).isFalse()
    }
}
