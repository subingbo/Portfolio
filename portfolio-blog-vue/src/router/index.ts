import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import BlogPage from '../pages/BlogPage.vue'
import PostDetailPage from '../pages/PostDetailPage.vue'
import PlaceholderPage from '../pages/PlaceholderPage.vue'
import ProjectsPage from '../pages/ProjectsPage.vue'
import ProjectDetailPage from '../pages/ProjectDetailPage.vue'
import LoginPage from '../pages/LoginPage.vue'
import RegisterPage from '../pages/RegisterPage.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: HomePage },
    { path: '/blog', name: 'blog', component: BlogPage },
    { path: '/post/:id', name: 'post', component: PostDetailPage },
    { path: '/projects', name: 'projects', component: ProjectsPage, meta: { title: '项目' } },
    {
      path: '/projects/:id',
      name: 'project-detail',
      component: ProjectDetailPage,
      meta: { title: '项目详情' },
    },
    {
      path: '/profile',
      name: 'profile',
      component: PlaceholderPage,
      meta: { title: '个人' },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
      meta: { title: '登录' },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterPage,
      meta: { title: '注册' },
    },
    {
      path: '/recent-comments',
      name: 'recent-comments',
      component: PlaceholderPage,
      meta: { title: '近期评论' },
    },
    {
      path: '/tags',
      name: 'tags',
      component: PlaceholderPage,
      meta: { title: '标签' },
    },
    {
      path: '/search',
      name: 'search',
      component: PlaceholderPage,
      meta: { title: '搜索' },
    },
  ],
})
