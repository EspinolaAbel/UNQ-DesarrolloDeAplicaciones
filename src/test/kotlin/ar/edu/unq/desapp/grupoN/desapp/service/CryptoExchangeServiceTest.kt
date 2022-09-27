package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.SymbolsEnum
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import ar.edu.unq.desapp.grupoN.desapp.model.dto.PriceWithDatetime
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.springframework.web.client.RestTemplate
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class  CryptoExchangeServiceTest {

    lateinit var mockRestTemplate: RestTemplate
    lateinit var service: CryptoExchangeService

    @BeforeTest
    fun setUp() {
        mockRestTemplate = mock(RestTemplate::class.java)
        service = CryptoExchangeService(mockRestTemplate, "baseUrl")
    }

    @Test
    fun givenASymbolWhenRequestingIts24hourPricesThenAListOfPricesIsReturned() {
        val date00minutes = Instant.parse("2022-01-01T00:00:00.000Z")
        val date15minutes = Instant.parse("2022-01-01T00:15:00.000Z")
        val cot1 = listOf<Any>("","","","","98.9","",date00minutes.epochSecond*1000)
        val cot2 = listOf<Any>("","","","","99.9","",date15minutes.epochSecond*1000)

        `when`(mockRestTemplate.getForObject<List<List<Any>>>(anyString(), any())).thenReturn(listOf( cot1, cot2 ))

        val actualCoinPrice = service.getSymbolPriceLast24hr(SymbolsEnum.AAVEUSDT)

        val expected = CoinPrices(SymbolsEnum.AAVEUSDT.name, listOf(
            PriceWithDatetime(98.9, date00minutes),
            PriceWithDatetime(99.9, date15minutes)
        ))

        assertEquals(expected, actualCoinPrice)
    }

}