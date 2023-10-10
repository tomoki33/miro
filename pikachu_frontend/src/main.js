import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import { library } from '@fortawesome/fontawesome-svg-core'
import { faUser } from '@fortawesome/free-solid-svg-icons' // 例としてユーザーアイコンをインポート
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

library.add(faUser) // ユーザーアイコンをライブラリに追加

const app = createApp(App)

// Font Awesomeのコンポーネントをグローバルコンポーネントとして追加
app.component('fa-icon', FontAwesomeIcon)

createApp(App).use(store).use(router).mount('#app')
