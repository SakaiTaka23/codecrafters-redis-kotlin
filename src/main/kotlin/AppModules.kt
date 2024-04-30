import commands.Get
import commands.Set
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
    single { Get() }
    single { Set() }
}

public val receiveModule: Reader = Reader(MainCommand(), Arguments())
public val respondModule: Responder = Responder(Encoder())
