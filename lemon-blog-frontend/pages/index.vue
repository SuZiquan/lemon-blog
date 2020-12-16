<script>
export default {
  middleware: [
    function ({ redirect }) {
      redirect('/article')
    },
  ],
  name: 'Home',
  methods: {
    storeToken(token) {
      if (process.browser) {
        localStorage.setItem('authToken', token)
      }
    },
  },
}
</script>
