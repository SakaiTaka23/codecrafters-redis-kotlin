import commands.Get
import commands.Set
import java.time.Clock
import java.time.ZoneId
import org.koin.core.module.Module
import org.koin.dsl.module
import presentor.Responder
import reciever.Reader
import repository.IStorage
import repository.InMemory
import resp.Decoder

public val appModule: Module = module {
    single<IStorage> { InMemory() }
    single<Clock> { Clock.system(ZoneId.of("Asia/Tokyo")) }
    single { Get() }
    single { Set() }
}

public val readerModule: Module = module {
    single { Decoder() }
    single { Reader() }
}

public val responderModule: Module = module {
    single { Responder() }
}
