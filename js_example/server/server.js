
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
var port = 3001
app.use(express.static('./'))
app.use(bodyParser.json({ limit: '50mb' }))
app.use(bodyParser.urlencoded({ limit: '50mb', extended: true }))
app.get('/status', function (req, res) {
    res.send({ status: 'ok' })
})

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})
