package me.cominixo.betterf3.config.gui.modules;

import com.mojang.blaze3d.vertex.PoseStack;
import me.cominixo.betterf3.config.ModConfigFile;
import me.cominixo.betterf3.modules.BaseModule;
import me.cominixo.betterf3.utils.PositionEnum;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Objects;

/**
 * The Modules screen.
 */
public class ModulesScreen extends Screen {

    /**
     * The parent screen.
     */
    Screen parent;
    /**
     * The Modules list widget.
     */
    ModuleListWidget modulesListWidget;
    private boolean initialized = false;

    private Button editButton, deleteButton;

    /**
     * The side of the screen (left or right).
     */
    public PositionEnum side;

    /**
     * Instantiates a new Modules screen.
     *
     * @param parent the parent screen
     * @param side   the side of the screen
     */
    public ModulesScreen(Screen parent, PositionEnum side) {
        super(new TranslatableComponent("config.betterf3.title.modules"));
        this.parent = parent;
        this.side = side;

    }
    @Override
    protected void init() {
        super.init();

        if (this.initialized) {
            this.modulesListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.modulesListWidget = new ModuleListWidget(this, this.minecraft, this.width, this.height, 32, this.height - 64, 36);
            if (this.side == PositionEnum.LEFT) {
                this.modulesListWidget.setModules(BaseModule.modules);
            } else if (this.side == PositionEnum.RIGHT) {
                this.modulesListWidget.setModules(BaseModule.modulesRight);
            }
        }

        this.addRenderableWidget(this.modulesListWidget);

        this.editButton = this.addRenderableWidget(new Button(this.width / 2 - 50, this.height - 50, 100, 20, new TranslatableComponent("config.betterf3.modules.edit_button"), (buttonWidget) -> {
            Screen screen =
                    (EditModulesScreen.getConfigBuilder(Objects.requireNonNull(this.modulesListWidget.getSelected()).module, this).build());
            assert minecraft != null;
            minecraft.setScreen(screen);
        }));

        this.addRenderableWidget(new Button(this.width / 2 + 4 + 50, this.height - 50, 100, 20, new TranslatableComponent("config.betterf3.modules.add_button"), (buttonWidget) -> {
            assert minecraft != null;
            minecraft.setScreen(AddModuleScreen.getConfigBuilder(this).build());
        }));

        this.deleteButton = this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 50, 100, 20, new TranslatableComponent("config.betterf3.modules.delete_button"), (buttonWidget) -> this.modulesListWidget.removeModule(this.modulesListWidget.moduleEntries.indexOf(Objects.requireNonNull(this.modulesListWidget.getSelected())))));

        this.addRenderableWidget(new Button(this.width / 2 - 154, this.height - 30 + 4, 300 + 8, 20, new TranslatableComponent("config.betterf3.modules.done_button"), (buttonWidget) -> {
            this.onClose();
            assert minecraft != null;
            minecraft.setScreen(parent);
        }));

        updateButtons();



    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.modulesListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredString(matrices, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);

    }

    @Override
    public void onClose() {
        if (this.side == PositionEnum.LEFT) {
            BaseModule.modules.clear();
            for (ModuleListWidget.ModuleEntry entry : this.modulesListWidget.moduleEntries) {
                BaseModule.modules.add(entry.module);
            }
        } else if (this.side == PositionEnum.RIGHT) {
            BaseModule.modulesRight.clear();
            for (ModuleListWidget.ModuleEntry entry : this.modulesListWidget.moduleEntries) {
                BaseModule.modulesRight.add(entry.module);
            }
        }
        assert this.minecraft != null;
        this.minecraft.setScreen(parent);
        ModConfigFile.saveRunnable.run();
    }

    /**
     * Selects a module.
     *
     * @param entry the entry
     */
    public void select(ModuleListWidget.ModuleEntry entry) {
        this.modulesListWidget.setSelected(entry);
        updateButtons();
    }

    /**
     * Updates the buttons.
     */
    public void updateButtons() {
        if (this.modulesListWidget.getSelected() != null) {
            editButton.active = true;
            deleteButton.active = true;
        } else {
            editButton.active = false;
            deleteButton.active = false;
        }
    }

}