const koa = require('koa');
const app = new koa();
const server = require('http').createServer(app.callback());
const WebSocket = require('ws');
const wss = new WebSocket.Server({server});
const Router = require('koa-router');
const cors = require('koa-cors');
const bodyParser = require('koa-bodyparser');
const convert = require('koa-convert');

app.use(bodyParser());
app.use(convert(cors()));
app.use(async (ctx, next) => {
    const start = new Date();
    await next();
    const ms = new Date() - start;
    console.log(`${ctx.method} ${ctx.url} ${ctx.response.status} - ${ms}ms`);
});

var sensorValue;

const router = new Router();

router.get('/', ctx => {
    console.log("GET");
    console.log(sensorValue);
    
    ctx.response.body = sensorValue;
    ctx.response.status = 200;
});

router.get('/updateStatus/:nr', ctx => {
    console.log("/updateStatus");

    const headers = ctx.params;
    console.log(headers.nr);
    
    console.log("ctx: " + JSON.stringify(ctx));
    console.log("body: " + JSON.stringify(headers));
    sensorValue = parseInt(headers.nr);
    ctx.response.status = 200;
});

app.use(router.routes());
app.use(router.allowedMethods());

server.listen(38176);
