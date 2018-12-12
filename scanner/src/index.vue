<template>
  <div class="wrapper">
    <scanner class="scanner" @decoded="handleResult"></scanner>
    <text class="result" ref="result">{{decodeResult}}</text>
  </div>
</template>

<script>
export default {
  name: 'App',
  components: {
  },
  data() {
    return {
      decodeResult: '',
    };
  },
  methods: {
    handleResult(e) {
      this.decodeResult = e.value;
    },
  }
}
</script>

<style scoped>
  .wrapper {
    width: 750px;
  }
  .scanner {
    width: 750px;
    height: 600px;
  }
  .result {
    margin-top: 70px;
    font-size: 40px;
    color: #000000;
  }
</style>
