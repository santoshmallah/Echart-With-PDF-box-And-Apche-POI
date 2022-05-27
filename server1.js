const http = require('http')
const hostname = process.env.HOST || '0.0.0.0'
const port = process.env.PORT || '8080'
const Canvas = require('canvas')
const echarts = require('echarts')

echarts.setCanvasCreator(function () {
    return Canvas.createCanvas(100, 100)
})

function renderChart(data) {
	console.log("Printing json request:",JSON.parse(data))
  const chart = echarts.init(Canvas.createCanvas(400, 400), {}, {
    devicePixelRatio: 2
  })
  chart.setOption(JSON.parse(data))
  return chart.getDom().toBuffer()
}

const server = http.createServer((req, res) => {  
  let data = ''
  req.on('data', chunk => data += chunk)
  req.on('end', () => {
    try {
		const w = parseInt(req.headers['x-chart-width'], 10) || 400
		const h = parseInt(req.headers['x-chart-height'], 10) || 400
      const imageData = renderChart(data,w,h)
      res.setHeader('Content-Type', 'image/png')
      res.write(imageData, 'binary')
      res.end(null, 'binary')
    } catch (e) {
      res.statusCode = 500
      res.end(e)
    }
  })
})
server.listen(port, hostname, () => {
  console.log('Serving at http://' + hostname + ':' + port + '/')
})

