package com.cout970.magneticraft

import com.cout970.magneticraft.config.ConfigHandler
import com.cout970.magneticraft.gui.GuiHandler
import com.cout970.magneticraft.proxy.CommonProxy
import com.cout970.magneticraft.util.LANG_ADAPTER
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.NAME
import com.cout970.magneticraft.util.VERSION
import com.cout970.magneticraft.world.WorldGenerator
import net.minecraft.client.Minecraft
import net.minecraft.util.Timer
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(
    modid = MODID,
    name = NAME,
    version = VERSION,
    modLanguage = "kotlin",
    modLanguageAdapter = LANG_ADAPTER
)
object Magneticraft {
    lateinit var log: Logger
    val network = SimpleNetworkWrapper(MODID)
    lateinit var configFile : File

    @SidedProxy(
        clientSide = "com.cout970.magneticraft.proxy.ClientProxy",
        serverSide = "com.cout970.magneticraft.proxy.CommonProxy"
    )
    lateinit var proxy: CommonProxy;

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        log = event.modLog
        configFile = event.suggestedConfigurationFile
        log.info("Starting pre-init")
        ConfigHandler.load()
        ConfigHandler.read()
        ConfigHandler.save()

        registerBlocks()
        registerItems()
        registerTileEntities()
        proxy.preInit()

        log.info("Pre-init done")
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        log.info("Starting init")

        proxy.init()
        WorldGenerator.init()
        GameRegistry.registerWorldGenerator(WorldGenerator, 10)
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GuiHandler)

        log.info("Init done")
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        log.info("Starting post-init")

        proxy.postInit()

        log.info("Post-init done")
    }

    //useful function to change the amount of tick per second used in minecraft
    //feel free to remove it if you want
    fun setTicksPerSecond(tps:Int){
        //DEBUG
        val timerField = Minecraft::class.java.getDeclaredField("timer")
        timerField.isAccessible = true
        val timer = timerField.get(Minecraft.getMinecraft()) as Timer
        val tickfield = Timer::class.java.getDeclaredField("ticksPerSecond")
        tickfield.isAccessible = true
        tickfield.set(timer, tps.toFloat())
        //DEBUG
    }
}