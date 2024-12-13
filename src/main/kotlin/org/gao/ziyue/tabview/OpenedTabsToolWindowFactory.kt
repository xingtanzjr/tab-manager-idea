package org.gao.ziyue.tabview

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class OpenedTabsToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // 创建 Tab 列表面板
        val tabListPanel = OpenedTabsPanel(project)

        // 将面板嵌入到 ToolWindow
        val content = toolWindow.contentManager.factory.createContent(tabListPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}