package net.ledestudio.example.mod.input;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import static net.ledestudio.example.mod.ExampleMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class InputMappingEventListener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(InputMapping.JUMP_KEY.get());
    }
}
