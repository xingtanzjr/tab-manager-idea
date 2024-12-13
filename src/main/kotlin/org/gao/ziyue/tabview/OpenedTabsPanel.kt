package org.gao.ziyue.tabview

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.components.JBList
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

class OpenedTabsPanel(private val project: Project) : JPanel() {
    private val tabListModel = mutableListOf<VirtualFile>() // 存储打开的文件列表
    private val tabListView = JBList<VirtualFile>().apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        selectedIndex = 0
    }

    init {
        layout = java.awt.BorderLayout()

        updateTabList()
        tabListView.setListData(tabListModel.toTypedArray())
        tabListView.cellRenderer = TabListCellRenderer()

        add(JScrollPane(tabListView), java.awt.BorderLayout.CENTER)

        tabListView.addListSelectionListener { event ->
            if (!event.valueIsAdjusting) {
                val selectedFile = tabListView.selectedValue
                selectedFile?.let {
                    FileEditorManager.getInstance(project).openFile(it, false)
                }
            }
        }

        val connection = project.messageBus.connect()
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                updateTabList()
            }

            override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
                updateTabList()
            }

            override fun selectionChanged(event: FileEditorManagerEvent) {
                updateTabList()
            }
        })
    }

    private fun updateTabList() {
        tabListModel.clear()
        val openFiles = FileEditorManager.getInstance(project).openFiles
        tabListModel.addAll(openFiles)
        tabListView.setListData(tabListModel.toTypedArray())

        val activeFile = FileEditorManager.getInstance(project).selectedFiles.firstOrNull()

        if (activeFile != null) {
            tabListView.setSelectedValue(activeFile, true)
        }
    }

    private class TabListCellRenderer : ColoredListCellRenderer<VirtualFile>() {
        override fun customizeCellRenderer(
            list: JList<out VirtualFile>,
            value: VirtualFile?,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) {
            if (value != null) {
                append(value.name)
                icon = value.fileType.icon
            } else {
                append("Unknown file")
            }
        }
    }
}