<template>
  <v-container fluid class="pa-0" style="margin-top: -64px;">
    <!-- Vuetify 輪播區塊 -->
    <v-carousel :interval="5000" cycle height="100vh" show-arrows class="custom-carousel">
      <v-carousel-item>
        <v-img src="/images/fh1.jpeg" cover height="100vh" />
      </v-carousel-item>
      <v-carousel-item>
        <v-img src="/images/fh2.png" cover height="100vh" />
      </v-carousel-item>
      <v-carousel-item>
        <v-img src="/images/fh3.png" cover height="100vh" />
      </v-carousel-item>
    </v-carousel>

    <!-- 歡迎文字 -->
    <v-container class="py-10">
      <h1 class="text-center text-h3 font-weight-bold">歡迎來到 First Hotel</h1>
      <p class="text-center text-subtitle-1">奢華住宿、無限享受。</p>
    </v-container>
    <div id="map" style="height: 500px;"></div>
  </v-container>
</template>

<script setup>
import { onMounted } from 'vue';
import { Loader } from "@googlemaps/js-api-loader";

onMounted(() => {
  const loader = new Loader({
    apiKey: "AIzaSyDX4tFmJbEb9l9pPoSiq736YdzJ8YSYqHs",
    version: "weekly",
  });

  loader.load().then(async () => {
    const { Map, InfoWindow } = await google.maps.importLibrary("maps");
    const { AdvancedMarkerElement } = await google.maps.importLibrary("marker");
    const map = new Map(document.getElementById("map"), {
      center: { lat: 24.985337778704295, lng: 121.22178621260504 },
      zoom: 15,
      mapId: "72db64ac2598077b",
    });

    const marker = new AdvancedMarkerElement({
      position: { lat: 24.985337778704295, lng: 121.22178621260504 },
      map: map,
      title: "資展國際JAVA中壢班",

    });
    // 建立 InfoWindow（飯店照片＋名字）
    const infoWindow = new InfoWindow({
      content: `
        <div style="text-align:center;">
          <h3>First Hotel</h3>
          <p>桃園市中壢區新生路2段421號</p>
          <a href="https://www.google.com/maps/place/%E8%81%96%E5%BE%B7%E5%9F%BA%E7%9D%A3%E5%AD%B8%E9%99%A2/@24.986193,121.21721,16.25z/data=!4m15!1m8!3m7!1s0x34682183c26cf389:0x6b7addbfc4686f61!2zMzIw5qGD5ZyS5biC5Lit5aOi5Y2A5paw55Sf6Lev5LqM5q61NDIx6Jmf!3b1!8m2!3d24.9853767!4d121.2211103!16s%2Fg%2F11c1k1j75z!3m5!1s0x34682183e7b783c3:0xf0ebfba2069b6158!8m2!3d24.9851188!4d121.2217137!16s%2Fg%2F155r7ck7?entry=ttu&g_ep=EgoyMDI1MDQxNi4xIKXMDSoJLDEwMjExNDUzSAFQAw%3D%3D"target="_blank">在Google地圖上查看</a>
          <img src="https://picsum.photos/200/100" alt="飯店圖片" style="width:200px;height:100px;object-fit:cover;border-radius:8px;margin-top:8px;display: block;">
        </div>
      `
    });

    // 綁定點擊事件：點 Marker 開 InfoWindow
    marker.addListener("gmp-click", () => {
      infoWindow.open({
        anchor: marker,
        map,
      });
    });

  });
});
</script>

<style scoped>
:deep(.custom-carousel .v-btn--icon) {
  background-color: transparent !important;
  box-shadow: none !important;
  border: none !important;
  min-width: unset !important;
  width: 40px;
  height: 40px;
  padding: 0;
  margin: 0;
  color: white !important;
  opacity: 1 !important;
}

:deep(.custom-carousel .v-btn--icon .v-icon) {
  font-size: 32px;
  color: white !important;
}

/* 縮小輪播下方小圈圈 */
:deep(.custom-carousel .v-carousel__controls .v-btn--icon) {
  width: 12px !important;
  height: 12px !important;
  min-width: unset !important;
  margin: 0 4px;
}

:deep(.custom-carousel .v-carousel__controls .v-btn--icon .v-icon) {
  font-size: 8px !important;
  opacity: 0.7;
}

/* 移除下方控制區塊的背景 */
:deep(.custom-carousel .v-carousel__controls) {
  background-color: transparent !important;
  backdrop-filter: none !important;
}
</style>