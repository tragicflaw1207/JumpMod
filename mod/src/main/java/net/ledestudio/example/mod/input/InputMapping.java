package net.ledestudio.example.mod.input;


import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class InputMapping {

    public static final Lazy<KeyMapping> JUMP_KEY = Lazy.of(() -> new KeyMapping(
            "차지 점프",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.movement"
    ));
}
