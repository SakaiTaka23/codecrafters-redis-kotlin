package commands

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import repository.Storage
import resp.Protocol
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

private const val KEY: String = "key"
private const val VALUE: String = "value"

public class SetTest : ShouldSpec({
    val repo = mockk<Storage>()
    val clock = Clock.fixed(Instant.parse("2024-04-01T00:00:00Z"), ZoneOffset.UTC)
    val set = Set(repo, clock)
    val now = Instant.now(clock)

    val argKey: CapturingSlot<String> = slot<String>()
    val argValue: CapturingSlot<String> = slot<String>()
    val argExpires: CapturingSlot<Instant?> = slot<Instant?>()

    should("be able to set values") {
        every { repo.set(capture(argKey), capture(argValue)) } returns Unit
        val command = Protocol(mutableListOf("set", KEY, VALUE))
        set.run(command)
        argKey.captured shouldBe KEY
        argValue.captured shouldBe VALUE
    }

    should("be able to set values with expiration") {
        every {
            repo.set(
                capture(argKey), capture(argValue),
                captureNullable(argExpires),
            )
        } returns Unit
        val command = Protocol(mutableListOf("set", KEY, VALUE, "px", "100"))
        set.run(command)
        argKey.captured shouldBe KEY
        argValue.captured shouldBe VALUE
        argExpires.captured shouldBe now.plusMillis(100)
    }
})
