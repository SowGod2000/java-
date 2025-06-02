import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'


// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

// 引入 icon 套件
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi',
    },
})

const app = createApp(App)

const pinia = createPinia()
app.use(vuetify)
app.use(router)
app.use(ElementPlus)
app.use(pinia)
app.mount('#app')

