package ar.edu.unq.desapp.grupoN.desapp.service

import ar.edu.unq.desapp.grupoN.desapp.model.Symbol
import ar.edu.unq.desapp.grupoN.desapp.model.dto.CoinPrices
import ar.edu.unq.desapp.grupoN.desapp.model.dto.PriceWithDatetime
import ar.edu.unq.desapp.grupoN.desapp.model.mapping.AdvertisementMapper
import ar.edu.unq.desapp.grupoN.desapp.persistence.AdvertisementRepository
import ar.edu.unq.desapp.grupoN.desapp.persistence.OperationRepository
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.MockitoAnnotations
import org.springframework.web.client.RestTemplate
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class  CryptoExchangeServiceTest {

    @Mock lateinit var mockRestTemplate: RestTemplate
    @Mock lateinit var mockAdvertisementRepository: AdvertisementRepository
    @Mock lateinit var mockOperationRepository: OperationRepository
    @Mock lateinit var mockAdvertisementMapper: AdvertisementMapper
    @Mock lateinit var mockUserService: UserService
    lateinit var service: CryptoExchangeService

    @BeforeTest
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        service = CryptoExchangeService(
            mockRestTemplate,
            mockAdvertisementRepository,
            mockAdvertisementMapper,
            mockOperationRepository,
            mockUserService,
            "baseUrl"
        )
    }

    @Test
    fun givenASymbolWhenRequestingIts24hourPricesThenAListOfPricesIsReturned() {
        val date00minutes = Instant.parse("2022-01-01T00:00:00.000Z")
        val date15minutes = Instant.parse("2022-01-01T00:15:00.000Z")
        val cot1 = listOf<Any>("","","","","98.9","",date00minutes.epochSecond*1000)
        val cot2 = listOf<Any>("","","","","99.9","",date15minutes.epochSecond*1000)

        `when`(mockRestTemplate.getForObject<List<List<Any>>>(anyString(), any())).thenReturn(listOf( cot1, cot2 ))

        val actualCoinPrice = service.getSymbolPriceLast24hr(Symbol.AAVEUSDT)

        val expected = CoinPrices(Symbol.AAVEUSDT.name, listOf(
            PriceWithDatetime(98.9, date00minutes),
            PriceWithDatetime(99.9, date15minutes)
        ))

        assertEquals(expected, actualCoinPrice)
    }

}