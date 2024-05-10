import commands.Get
import commands.Set
import java.time.Clock
import java.time.ZoneId
import kotlinx.coroutines.channels.Channel
import org.koin.core.module.Module
import org.koin.dsl.module
import presentor.Responder
import reciever.Reader
import replicator.Propagator
import repository.IStorage
import repository.InMemory
import resp.Decoder
import resp.Protocol

public val appModule: Module = module {
    single<IStorage> { InMemory() }
    single<Clock> { Clock.system(ZoneId.of("Asia/Tokyo")) }
    single { Get() }
    single { Set() }
}

public val propagateModule: Module = module {
    single { Propagator() }
    single { propagateChannel }
}

public val readerModule: Module = module {
    single { Decoder() }
    single { Reader() }
}

public val responderModule: Module = module {
    single { Responder }
}

private val propagateChannel: Channel<Protocol> = Channel(Channel.UNLIMITED)
