<template>
  <router-view />
</template>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  min-width: 320px;
  color: #1f2937;
  background: #f5f7fb;
  font-family: Inter, 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}

a { color: inherit; text-decoration: none; }

.page-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.page-panel {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 14px 32px rgba(31, 41, 55, 0.06);
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
}
</style>
