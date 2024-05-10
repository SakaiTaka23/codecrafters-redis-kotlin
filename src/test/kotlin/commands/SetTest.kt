package commands

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import repository.Storage
import resp.Protocol
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private const val KEY: String = "key"
private const val VALUE: String = "value"

public class SetTest : KoinTest {
    private val repo: Storage = mockk<Storage>()
    private val fixedClock = Clock.fixed(Instant.parse("2024-04-01T00:00:00Z"), ZoneOffset.UTC)
    private val now = Instant.now(fixedClock)
    private val argKey: CapturingSlot<String> = slot<String>()
    private val argValue: CapturingSlot<String> = slot<String>()
    private val argExpires: CapturingSlot<Instant?> = slot<Instant?>()

    @BeforeTest
    public fun beforeTest() {
        startKoin {
            modules(
                module {
                    single<Storage> { repo }
                    single<Clock> { fixedClock }
                }
            )
        }
    }

    @AfterTest
    public fun afterTest() {
        stopKoin()
    }

    @Test
    public fun `can set value (without expiration)`() {
        every { repo.set(capture(argKey), capture(argValue)) } returns Unit
        val command = Protocol(mutableListOf("set", KEY, VALUE))

        Set().run(command)

        assertEquals(KEY, argKey.captured)
        assertEquals(VALUE, argValue.captured)
    }

    @Test
    public fun `can set value (with expiration)`() {
        every { repo.set(capture(argKey), capture(argValue), captureNullable(argExpires)) } returns Unit
        val command = Protocol(
            mutableListOf("set", KEY, VALUE, "px", "100")
        )

        Set().run(command)

        assertEquals(KEY, argKey.captured)
        assertEquals(VALUE, argValue.captured)
        assertEquals(now.plusMillis(100), argExpires.captured)
    }
}
