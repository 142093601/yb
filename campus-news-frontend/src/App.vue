<template>
  <router-view />
</template>

<style>
/* Global Reset & Base Styles */
*,
*::before,
*::after {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: #f5f7fa;
  color: #333;
}

a {
  text-decoration: none;
  color: inherit;
}

img {
  max-width: 100%;
}

/* Smooth scrollbar */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* Element Plus overrides */
.el-button--primary {
  --el-button-bg-color: #4f6ef7;
  --el-button-border-color: #4f6ef7;
  --el-button-hover-bg-color: #3d5ce5;
  --el-button-hover-border-color: #3d5ce5;
}
</style>
