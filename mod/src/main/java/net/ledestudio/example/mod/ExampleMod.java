package net.ledestudio.example.mod;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// 여기 값은 META-INF/neoforge.mods.toml 파일의 항목과 일치해야 합니다.
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // 모든 참조를 위한 공통 위치에 모드 ID를 정의합니다.
    public static final String MODID = "examplemod";
    // slf4j 로거를 직접 참조합니다.
    private static final Logger LOGGER = LogUtils.getLogger();
    // "examplemod" 네임스페이스 아래에 등록될 블록을 보유할 Deferred Register를 생성합니다.
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // "examplemod" 네임스페이스 아래에 등록될 아이템을 보유할 Deferred Register를 생성합니다.
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // "examplemod" 네임스페이스 아래에 등록될 CreativeModeTabs를 보유할 Deferred Register를 생성합니다.
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 네임스페이스와 경로를 결합하여 "examplemod:example_block" ID를 가진 새로운 블록을 생성합니다.
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 네임스페이스와 경로를 결합하여 "examplemod:example_block" ID를 가진 새로운 BlockItem을 생성합니다.
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // ID가 "examplemod:example_id"인 새로운 음식 아이템을 생성합니다. 영양분 1, 포화도 2입니다.
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // ID가 "examplemod:custom_boots"인 새로운 커스텀 부츠 아이템을 생성합니다.
    public static final DeferredItem<ArmorItem> CUSTOM_BOOTS = ITEMS.register("custom_boots",
            () -> new ArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties()));

    // 예제 아이템을 위한 ID가 "examplemod:example_tab"인 새로운 크리에이티브 탭을 생성하며, 전투 탭 뒤에 배치됩니다.
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) // CreativeModeTab의 제목에 대한 언어 키
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> new ItemStack(Items.NETHERITE_BOOTS))
            .displayItems((parameters, output) -> {
                output.accept(CUSTOM_BOOTS.get()); // 탭에 커스텀 부츠를 추가합니다.
            }).build());

    // 모드 클래스의 생성자는 모드가 로드될 때 실행되는 첫 번째 코드입니다.
    // FML은 IEventBus나 ModContainer와 같은 일부 매개변수 유형을 자동으로 전달합니다.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // 모드 로딩을 위한 commonSetup 메서드를 등록합니다.
        modEventBus.addListener(this::commonSetup);

        // 블록이 등록되도록 모드 이벤트 버스에 Deferred Register를 등록합니다.
        BLOCKS.register(modEventBus);
        // 아이템이 등록되도록 모드 이벤트 버스에 Deferred Register를 등록합니다.
        ITEMS.register(modEventBus);
        // 탭이 등록되도록 모드 이벤트 버스에 Deferred Register를 등록합니다.
        CREATIVE_MODE_TABS.register(modEventBus);

        // 관심 있는 서버 및 기타 게임 이벤트에 대해 자신을 등록합니다.
        // 이 클래스(ExampleMod)가 이벤트에 직접 응답하도록 하려면 이 줄이 필요합니다.
        // onServerStarting()과 같은 @SubscribeEvent로 주석이 달린 함수가 없는 경우 이 줄을 추가하지 마세요.
        NeoForge.EVENT_BUS.register(this);

        // 아이템을 크리에이티브 탭에 등록합니다.
        modEventBus.addListener(this::addCreative);

        // FML이 설정 파일을 생성하고 로드할 수 있도록 모드의 ModConfigSpec을 등록합니다.
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 일부 공통 설정 코드
        LOGGER.info("공통 설정에서 안녕하세요");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // 예제 블록 아이템을 건축 블록 탭에 추가합니다.
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // SubscribeEvent를 사용하여 이벤트 버스가 호출할 메서드를 자동으로 검색할 수 있습니다.
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // 서버가 시작될 때 작업을 수행합니다.
        LOGGER.info("서버 시작에서 안녕하세요");
    }

    // EventBusSubscriber를 사용하여 @SubscribeEvent로 주석이 달린 모든 정적 메서드를 자동으로 등록할 수 있습니다.
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 일부 클라이언트 설정 코드
            LOGGER.info("클라이언트 설정에서 안녕하세요");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
