<template>
  <!-- 路由出口：当前 URL 对应的页面组件会显示在这里。 -->
  <router-view />
</template>

<style>
/* 全局重置：清除浏览器默认边距，并让元素尺寸包含边框和内边距。 */
* { margin: 0; padding: 0; box-sizing: border-box; }

/* 页面基础样式：字体、文字颜色和全局背景。 */
body {
  min-width: 320px;
  color: #1f2937;
  background: #f5f7fb;
  font-family: Inter, 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

a { color: inherit; text-decoration: none; }

/* 后台页面公共外壳：多个管理页面复用这个纵向布局。 */
.page-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 公共面板：为表格和表单提供统一的边框、背景与阴影。 */
.page-panel {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 14px 32px rgba(31, 41, 55, 0.06);
}

/* 公共工具栏：标题放左侧，搜索和按钮放右侧。 */
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

/* 各功能区的小标题。 */
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}
</style>
