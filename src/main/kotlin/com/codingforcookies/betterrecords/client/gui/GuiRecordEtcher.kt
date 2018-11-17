package com.codingforcookies.betterrecords.client.gui

import com.codingforcookies.betterrecords.ID
import com.codingforcookies.betterrecords.ModConfig
import com.codingforcookies.betterrecords.api.library.Song
import com.codingforcookies.betterrecords.api.library.urlExistsInAnyLibrary
import com.codingforcookies.betterrecords.block.tile.TileRecordEtcher
import com.codingforcookies.betterrecords.client.ClientProxy
import com.codingforcookies.betterrecords.client.gui.parts.GuiButtonSong
import com.codingforcookies.betterrecords.library.Libraries
import com.codingforcookies.betterrecords.library.LocalLibrary
import com.codingforcookies.betterrecords.network.PacketHandler
import com.codingforcookies.betterrecords.network.PacketURLWrite
import com.codingforcookies.betterrecords.util.BetterUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiButtonImage
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GuiRecordEtcher(inventoryPlayer: InventoryPlayer, val tileEntity: TileRecordEtcher) : GuiContainer(ContainerRecordEtcher(inventoryPlayer, tileEntity)) {

    val GUI = ResourceLocation(ID, "textures/gui/recordetcher.png")
    val BUTTONS = ResourceLocation(ID, "textures/gui/buttons.png")

    lateinit var nameField: GuiTextField
    lateinit var urlField: GuiTextField
    private var color = 0xFFFFFF
    private var author = Minecraft.getMinecraft().player.name

    /**
     * The current status of the GUI. This is updated in [updateStatus]
     */
    private var status = Status.NO_RECORD

    var selectedLibrary = Libraries.libraries.first()
    val selectedLibraryIndex get() = Libraries.libraries.indexOf(selectedLibrary)
    val maxLibraryIndex get() = Libraries.libraries.lastIndex

    var pageIndex = 0
    val maxPageIndex: Int
        get() {
            val x = Math.ceil(selectedLibrary.songs.size / 9.0).toInt() - 1
            return if (x > 0) x else 0
        }

    init {
        xSize = 292
    }

    override fun initGui() {
        super.initGui()

        nameField = GuiTextField(1, fontRenderer, 44, 20, 124, 10)
        urlField = GuiTextField(2, fontRenderer, 44, 35, 124, 10)
        urlField.maxStringLength = 256

        // Add main buttons
        buttonList.addAll(listOf(
                // GUI Button image params: id, x, y, width, height, xtexstart, ytexstart, ydifftext
                // Library Left Button
                GuiButtonImage(0, guiLeft + 175, guiTop + 20, 20, 9, 0, 0, 0, BUTTONS),
                // Library Right Button
                GuiButtonImage(1, guiLeft + 265, guiTop + 20, 20, 9, 20, 0, 0, BUTTONS),
                // Page Left Button
                GuiButtonImage(2, guiLeft + 175, guiTop + 150, 20, 9, 0, 0, 0, BUTTONS),
                // Page Right Button
                GuiButtonImage(3, guiLeft + 265, guiTop + 150, 20, 9, 20, 0, 0, BUTTONS),
                // Etch Button
                GuiButton(4, guiLeft + 44, guiTop + 50, 31, 20, "Etch")
        ))

        val blankEntry = Song("", "", "", "FFFFFF") // Blank entry to init buttons with
        // Buttons 10-18 are our List buttons
        (0 until 9).forEach { i ->
            buttonList.add(GuiButtonSong(10 + i, guiLeft + 176, guiTop + 31 + 13 * i, 108, 13, "", blankEntry))
        }

        updateListButtons()
    }

    /**
     * Update the info attached to buttons 10-18, which are our list buttons
     */
    private fun updateListButtons() {
        buttonList
                .filterIsInstance<GuiButtonSong>()
                .forEachIndexed { i, it ->
                    val indexToGet = i + pageIndex * 9

                    val entry = selectedLibrary.songs.getOrNull(indexToGet)

                    if (entry != null) {
                        it.entry = entry
                        it.displayString = entry.name
                        it.visible = true
                    } else {
                        it.visible = false
                    }
                }
    }

    /**
     * Clear any user entries to the GUI, minus the state of the library view.
     */
    private fun resetGUI() {
        nameField.text = ""
        urlField.text = ""
        color = 0xFFFFFF
        author = Minecraft.getMinecraft().player.name
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        resetCheck()

        when {
            nameField.isFocused -> nameField.textboxKeyTyped(typedChar, keyCode)
            urlField.isFocused -> urlField.textboxKeyTyped(typedChar, keyCode)
            else -> super.keyTyped(typedChar, keyCode)
        }
    }

    private fun resetCheck() {
        checkedURL = false
        checkURLTime = System.currentTimeMillis() + 2000
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        val x = mouseX - (width - xSize) / 2
        val y = mouseY - (height - ySize) / 2

        nameField.mouseClicked(x, y, mouseButton)
        urlField.mouseClicked(x, y, mouseButton)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> { // Library Left
                selectedLibrary = Libraries.libraries[BetterUtils.wrapInt(selectedLibraryIndex - 1, 0, maxLibraryIndex)]
                pageIndex = 0
                updateListButtons()
            }
            1 -> { // Library Right
                selectedLibrary = Libraries.libraries[BetterUtils.wrapInt(selectedLibraryIndex + 1, 0, maxLibraryIndex)]
                pageIndex = 0
                updateListButtons()
            }
            2 -> { // Page Left
                pageIndex = BetterUtils.wrapInt(pageIndex - 1, 0, maxPageIndex)
                updateListButtons()
            }
            3 -> { // Page Right
                pageIndex = BetterUtils.wrapInt(pageIndex + 1, 0, maxPageIndex)
                updateListButtons()
            }
            4 -> { // Etch Button
                if (status == Status.READY) {
                    PacketHandler.sendToServer(PacketURLWrite(
                            tileEntity.pos,
                            URL(urlField.text).openConnection().contentLength / 1024 / 1024,
                            nameField.text.trim(),
                            urlField.text,
                            color,
                            author
                    ))

                    val etchingsLibrary = Libraries.libraries
                            .find { it is LocalLibrary && it.file.name == "myEtchings.json" }
                    etchingsLibrary?.let {
                        if (!urlExistsInAnyLibrary(urlField.text)) {
                            it.songs.add(Song(
                                    nameField.text.trim(),
                                    author,
                                    urlField.text,
                                    "FFFFFF"
                            ))
                            (it as LocalLibrary).save()
                        }
                    }

                    updateListButtons()
                    resetGUI()
                }
            }
            in 10..18 -> { // One of the Library buttons
                if (button is GuiButtonSong) { // Should be, we just want smart casting.
                    nameField.text = button.entry.name
                    urlField.text = button.entry.url
                    color = button.entry.colorInt
                    author = button.entry.author
                }

                resetCheck()
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        with(fontRenderer) {
            drawString(I18n.format("tile.betterrecords:recordetcher.name"), 8, 6, 4210752)
            //drawString(I18n.format("container.inventory"), 8, 72, 4210752)
            drawString(I18n.format("gui.betterrecords.label.name") + ": ", 10, 21, 4210752)
            drawString(I18n.format("gui.betterrecords.label.url") + ": ", 10, 36, 4210752)

            drawString(selectedLibrary.name, xSize - 5 - getStringWidth(selectedLibrary.name), 8, 4210752)

            val libraryPageString = "${Libraries.libraries.indexOf(selectedLibrary) + 1}/${Libraries.libraries.lastIndex + 1}"
            drawString(libraryPageString, 212 + getStringWidth(libraryPageString) / 2, 20, 4210752)

            val pageString = "${pageIndex + 1}/${maxPageIndex + 1}"
            drawString(pageString, 212 + getStringWidth(pageString) / 2, 151, 4210752)

            val statusColor = when (status) {
                Status.READY -> 0x229922
                else -> 0x992222
            }
            drawString(status.message, 172 - getStringWidth(status.message), 72, statusColor)
        }

        nameField.drawTextBox()
        urlField.drawTextBox()

        updateStatus()
    }

    // Fields required for [updateStatus] to do its thing.
    private var checkedURL = false
    private var checkURLTime = 0L

    /**
     * Update the current status of the GUI.
     *
     * Currently, this is called form [drawGuiContainerForegroundLayer]
     */
    private fun updateStatus() {
        if (tileEntity.record.isEmpty) {
            status = Status.NO_RECORD
        } else if (tileEntity.record.hasTagCompound() && tileEntity.record.tagCompound!!.hasKey("url")) {
            status = Status.NOT_BLANK_RECORD
        } else if (nameField.text.isEmpty()) {
            status = Status.NO_NAME
        } else if (nameField.text.length < 3) {
            status = Status.NAME_TOO_SHORT
        } else if (urlField.text.isEmpty()) {
            status = Status.NO_URL
        } else if (!checkedURL) {
            status = Status.VALIDATING
            if (checkURLTime < System.currentTimeMillis()) {
                checkURLTime = 0
                val url: URL
                try {
                    url = URL(urlField.text.replace(" ", "%20"))
                    val connection = url.openConnection()
                    if (connection is HttpURLConnection) {
                        connection.requestMethod = "HEAD"
                        connection.connect()
                        if (connection.responseCode == 200) {
                            if (connection.getContentLength() / 1024 / 1024 > (if (ModConfig.client.downloadMax != 100) ModConfig.client.downloadMax else 102400)) {
                                status = Status.FILE_TOO_BIG.formatParams(ModConfig.client.downloadMax)
                            }
                        } else {
                            status = Status.INVALID_URL
                        }
                    } else {
                        if (Minecraft.getMinecraft().world.isRemote) {
                            connection.connect()
                            if (connection.contentLength == 0) {
                                status = Status.INVALID_FILE
                            }
                        } else {
                            status = Status.MULTIPLAYER
                        }
                    }
                    if (status == Status.VALIDATING) {
                        val contentType = connection.contentType
                        status = if (ClientProxy.encodings.contains(contentType))
                            Status.READY
                        else
                            Status.INVALID_FILE_ENCODING.formatParams(contentType)
                    }
                } catch (e: MalformedURLException) {
                    status = Status.INVALID_URL
                } catch (e: StringIndexOutOfBoundsException) {
                    status = Status.INVALID_URL
                } catch (e: IllegalArgumentException) {
                    status = Status.INVALID_URL
                } catch (e: IOException) {
                    status = Status.IO_EXCEPTION
                } finally {
                    checkedURL = true
                }
            }
        }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        mc.renderEngine.bindTexture(GUI)
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0F, 0F, xSize, ySize, 292F, 292F)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        renderHoveredToolTip(mouseX, mouseY)
    }

    /**
     * Enum representing every possible status of our GUI,
     * as well as translation keys.
     *
     * Kind of ugly, but is better than what we had before.
     */
    private enum class Status(val translateKey: String) {

        VALIDATING("gui.betterrecords.status.validating"),
        NO_RECORD("gui.betterrecords.recordetcher.status.noRecord"),
        NOT_BLANK_RECORD("gui.betterrecords.recordetcher.status.notBlank"),
        NO_NAME("gui.betterrecords.status.noName"),
        NAME_TOO_SHORT("gui.betterrecords.status.nameTooShort"),
        NO_URL("gui.betterrecords.status.noUrl"),
        INVALID_URL("gui.betterrecords.status.invalidUrl"),
        FILE_TOO_BIG("gui.betterrecords.recordetcher.status.fileTooBig"),
        INVALID_FILE("gui.betterrecords.recordetcher.status.invalidFile"),
        INVALID_FILE_ENCODING("gui.betterrecords.recordetcher.status.encodingNotSupported"),
        MULTIPLAYER("gui.betterrecords.recordetcher.status.multiplayer"),
        IO_EXCEPTION("gui.betterrecords.status.ioException"),
        READY("gui.betterrecords.recordetcher.ready");

        var arg: Any = ""

        fun formatParams(arg: Any) = this.apply {
            this.arg = ""
            this.arg = arg
        }

        val message: String get() = I18n.format(translateKey, arg)
    }
}
