import commands.Get
import commands.Set
import java.time.Clock
import java.time.ZoneId
import org.koin.core.module.Module
import org.koin.dsl.module
import presentor.Encoder
import presentor.Responder
import reciever.Arguments
import reciever.MainCommand
import reciever.Reader
import repository.IStorage
import repository.InMemory

public val appModule: Module = module {
    single<IStorage> { InMemory() }
    single<Clock> { Clock.system(ZoneId.of("Asia/Tokyo")) }
    single { Get() }
    single { Set() }
}

public val receiveModule: Reader = Reader(MainCommand(), Arguments())
public val respondModule: Responder = Responder(Encoder())