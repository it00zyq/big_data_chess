import Vue from 'vue'
import Router from 'vue-router'
import Index from "../components/Index";

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/index',
      Component: Index,
      name: 'Index'
    }
  ]
})
