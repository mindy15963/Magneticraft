package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import com.cout970.magneticraft.block.core.IOnActivated
import com.cout970.magneticraft.block.core.OnActivatedArgs
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

class ModuleToggleFluidExporter(
        tank: Tank,
        ports: () -> List<Pair<BlockPos, EnumFacing>>, // Facing is the side of the block to fill
        override val name: String = "module_toggle_fluid_exporter"
) : IModule, IOnActivated {

    val exporter = ModuleFluidExporter(tank, ports)
    var enable: Boolean = false

    override lateinit var container: IModuleContainer

    override fun init() {
        exporter.container = container
        exporter.init()
    }

    override fun update() {
        if (enable) {
            exporter.update()
        }
    }

    override fun onActivated(args: OnActivatedArgs): Boolean = args.run {
        val stack = playerIn.getHeldItem(hand)
        if (stack.isEmpty || !WrenchRegistry.isWrench(stack)) return false

        if (worldIn.isServer) {
            enable = !enable
            container.sendUpdateToNearPlayers()
        }
        return true
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        enable = nbt.getBoolean("enable")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("enable", enable)
    }
}