package net.ledestudio.example.mod.input;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.ledestudio.example.mod.ExampleMod;

import static net.ledestudio.example.mod.ExampleMod.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class InputEventListener {

    private static int charge = 0; // 충전 상태를 나타내는 변수
    private static boolean charging = false; // 현재 충전 중인지를 나타내는 변수
    private static int lastCharge = 0; // 마지막 충전 상태를 기억하는 변수
    private static boolean isChargingJump = false; // 현재 차징 점프 중인지 여부를 나타내는 변수
    private static int chargingJumpCooldown = 0; // 차징 점프 상태 유지 타이머

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onClickTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance(); // 현재 Minecraft 인스턴스를 가져옴.
        if (mc.player != null) { // 플레이어가 존재하는지 확인.
            if (InputMapping.JUMP_KEY.get().isDown()) { // 정의된 특수 점프 키가 눌렸는지 확인.
                ItemStack boots = mc.player.getInventory().getArmor(0); // 플레이어의 부츠 슬롯(0번 슬롯)의 아이템을 인식.
                if (boots.is(ExampleMod.CUSTOM_BOOTS.get())) { // 부츠가 CUSTOM_BOOTS 인지 확인.
                    if (!isChargingJump) { // 차징 점프 중이 아닌 경우에만 충전 시작.
                        charging = true; // 충전 시작.
                    }
                } else {
                    mc.player.displayClientMessage(Component.literal("특수 신발이 아닙니다!"), true); // 부적절한 부츠 착용 메시지를 표시.
                    charging = false; // 충전 중지.
                    charge = 0; // 충전 상태 초기화.
                }
            } else if (charging && !isChargingJump) {
                float jumpHeight = (charge / 100.0f) * 2.0f; // 충전 상태에 따라 점프 높이 계산.
                mc.player.jumpFromGround(); // 플레이어를 점프.
                mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(0, jumpHeight, 0)); // 계산된 점프 높이를 적용.
                charging = false; // 충전 상태 초기화.
                charge = 0; // 충전 상태 초기화.
                lastCharge = 0; // 마지막 충전 상태 초기화.
                isChargingJump = true; // 차징 점프 중으로 설정.
                chargingJumpCooldown = 1; // 차징 점프 상태 유지 타이머 설정.
                mc.player.displayClientMessage(Component.literal("Charging Jump!"), true); // 차징 점프 메시지 표시.
            }

            if (charging) { // 충전 중이고 차징 점프 중이 아닌 경우에만 충전 상태 증가.
                charge++; // 충전 상태 증가.
                if (charge > 100) {
                    charge = 100; // 충전 상태 최대값은 100.
                }
            }

            if (charge != lastCharge) { // 충전 상태가 변경된 경우.
                mc.player.displayClientMessage(Component.literal("Charging: " + charge + "%"), true); // 현재 충전 상태를 표시.
                lastCharge = charge; // 마지막 충전 상태 업데이트.
            }

            if (isChargingJump) {
                if (chargingJumpCooldown > 0) {
                    chargingJumpCooldown--; // 차징 점프 상태 유지 타이머 감소.
                } else if (mc.player.onGround()) { // 타이머가 만료되고 플레이어가 땅에 있는 경우.
                    isChargingJump = false; // 차징 점프 상태 초기화.
                }
            }
        }
    }
}
