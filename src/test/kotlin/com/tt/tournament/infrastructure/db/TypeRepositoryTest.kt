package com.tt.tournament.infrastructure.db

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TypeRepositoryTest {

    @Autowired
    private lateinit var typeRepository: TypeRepository

    @Test
    fun `Given type details when creating then return new type ID and type is persisted`() {
        // given
        val typeName = "Test Competition ${System.currentTimeMillis()}"
        val startGebuehr = 5.0

        // when
        val typeId = typeRepository.create(typeName, startGebuehr)

        // then
        assertThat(typeId).isGreaterThan(0)

        val createdType = typeRepository.findByName(typeName)
        assertThat(createdType).isNotNull
        assertThat(createdType?.id).isEqualTo(typeId)
        assertThat(createdType?.name).isEqualTo(typeName)
        assertThat(createdType?.startGebuehr).isEqualTo(startGebuehr)
        assertThat(createdType?.active).isEqualTo(1)
    }

    @Test
    fun `Given type name with default fee when creating then use 0 as start fee`() {
        // given
        val typeName = "Free Competition ${System.currentTimeMillis()}"

        // when
        val typeId = typeRepository.create(typeName)

        // then
        assertThat(typeId).isGreaterThan(0)

        val createdType = typeRepository.findByName(typeName)
        assertThat(createdType).isNotNull
        assertThat(createdType?.startGebuehr).isEqualTo(0.0)
    }

    @Test
    fun `Given created type when finding by name then return TypeEntity`() {
        // given
        val typeName = "Lookup Test ${System.currentTimeMillis()}"
        typeRepository.create(typeName, 3.5)

        // when
        val result = typeRepository.findByName(typeName)

        // then
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo(typeName)
        assertThat(result?.startGebuehr).isEqualTo(3.5)
    }
}
