<template>
    <v-app>
        <v-navigation-drawer app permanent dark color="#1e1e1e">
            <v-list>
                <v-list-item>
                    <v-list-item-title class="text-h6">後台選單</v-list-item-title>
                </v-list-item>
                <v-divider></v-divider>

                <!-- 儀表板 -->
                <v-list-item link :to="'/backend'" exact>
                    <v-icon>mdi-home</v-icon>
                    <v-list-item-title>後台首頁</v-list-item-title>
                </v-list-item>
                <!-- 會員管理 -->
                <v-list-item link :to="'/backend/memberlist'">
                    <v-icon>mdi-bed</v-icon>
                    <v-list-item-title>會員管理</v-list-item-title>
                </v-list-item>
                <!-- 房型管理 -->
                <v-list-item link :to="'/backend/roomtype'">
                    <v-icon>mdi-bed</v-icon>
                    <v-list-item-title>房型管理</v-list-item-title>
                </v-list-item>

                <!-- 訂房訂單管理 -->
                <v-list-item link :to="'/backend/bookingOrder'">
                    <v-icon icon="mdi-format-list-bulleted"></v-icon>
                    <v-list-item-title>訂房訂單管理</v-list-item-title>
                </v-list-item>

                <!-- 訂房圖表管理 -->
                <v-list-item link :to="'/backend/bookingChart'">
                    <v-icon icon="mdi-chart-line"></v-icon>
                    <v-list-item-title>訂房圖表管理</v-list-item-title>
                </v-list-item>

                <!-- 餐廳
                <v-list-group prepend-icon="mdi-silverware-fork-knife" value="false">
                    <template #activator="{ props }">
                        <v-list-item v-bind="props">
                            <v-list-item-title>餐廳訂位管理</v-list-item-title>
                        </v-list-item>
                    </template>

                    
                    <v-list-item link :to="'/backend/restaurant/restaurantOrder'">
                        <v-icon>mdi-format-list-bulleted</v-icon>
                        <v-list-item-title>訂位管理</v-list-item-title>
                    </v-list-item>

                    
                    <v-list-item link :to="'/backend/restaurant/restaurantOrderChart'">
                        <v-icon>mdi-chart-line</v-icon>
                        <v-list-item-title>訂位圖表</v-list-item-title>
                    </v-list-item>
                </v-list-group> -->


                  <!-- 餐廳訂位管理 -->
                  <v-list-item link :to="'/backend/restaurant/restaurantOrder'">
                        <v-icon>mdi-format-list-bulleted</v-icon>
                        <v-list-item-title>餐廳訂位管理</v-list-item-title>
                    </v-list-item>

                    <!-- 餐廳圖形管理 -->
                    <v-list-item link :to="'/backend/restaurant/restaurantOrderChart'">
                        <v-icon>mdi-chart-line</v-icon>
                        <v-list-item-title>餐廳訂位圖表</v-list-item-title>
                    </v-list-item>


                    <v-list-item link :to="'/backend/product'">
                        <v-icon>mdi-package-variant</v-icon>
                        <v-list-item-title>商城商品管理</v-list-item-title>
                    </v-list-item>
                    

                    <v-list-item link :to="'/backend/order'">
                        <v-icon>mdi-package-variant</v-icon>
                        <v-list-item-title>商城訂單管理</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/dashboard'">
                        <v-icon>mdi-chart-line</v-icon>
                        <v-list-item-title>商城銷售儀表板</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/tags'">
                        <v-icon>mdi-tag</v-icon>
                        <v-list-item-title>標籤管理</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/categories'">
                        <v-icon>mdi-folder-multiple-outline</v-icon>
                        <v-list-item-title>分類管理</v-list-item-title>
                    </v-list-item>
                <!-- <v-list-group prepend-icon="mdi-view-dashboard" value="true">
                    <template #activator="{ props }">
                        
                    <v-list-item v-bind="props" link :to="'/backend/product'">
                        <v-icon>mdi-package-variant</v-icon>
                        <v-list-item-title>商城商品管理</v-list-item-title>
                    </v-list-item>
                    </template>

                    <v-list-item link :to="'/backend/order'">
                        <v-icon>mdi-package-variant</v-icon>
                        <v-list-item-title>商城訂單管理</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/dashboard'">
                        <v-icon>mdi-chart-line</v-icon>
                        <v-list-item-title>商城銷售儀表板</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/tags'">
                        <v-icon>mdi-tag</v-icon>
                        <v-list-item-title>標籤管理</v-list-item-title>
                    </v-list-item>

                    <v-list-item link :to="'/backend/categories'">
                        <v-icon>mdi-folder-multiple-outline</v-icon>
                        <v-list-item-title>分類管理</v-list-item-title>
                    </v-list-item>
                </v-list-group> -->


                <!-- 回前台首頁 -->
                <v-list-item link :to="'/'">
                    <v-icon>mdi-arrow-left-bold</v-icon>
                    <v-list-item-title>回前台首頁</v-list-item-title>
                </v-list-item>

            </v-list>
        </v-navigation-drawer>

        <v-main>
            <router-view />
        </v-main>
    </v-app>
</template>

<script setup>
// 無需 script
</script>