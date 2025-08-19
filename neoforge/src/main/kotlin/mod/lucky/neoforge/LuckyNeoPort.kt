package mod.lucky.neoforge

import net.neoforged.fml.common.Mod
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge

@Mod("lucky")
class LuckyNeoPort(modBus: IEventBus) {
    init {
        // TODO: register your DeferredRegisters here on the MOD bus, e.g.:
        // MyBlocks.REGISTER.register(modBus)
        // MyItems.REGISTER.register(modBus)

        // TODO: gameplay events on the GAME bus:
        // NeoForge.EVENT_BUS.addListener(::onBlockBreak)
    }
}
