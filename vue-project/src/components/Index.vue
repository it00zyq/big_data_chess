<template>
  <div class="hello">
    <div style="position:absolute;z-index:1"><img src="../assets/bg.jpg" alt=""></div>
    <div class="box">
      <div class="chessborder" v-for="item in chessBorders" :key="item.id" @click="moveTo(item.X,item.Y)">
        <div class="chessman" v-if="item.name" @click="selectItem(item.X,item.Y,item.name,item.color)"
             :style={color:item.color,borderColor:item.BDcolor}>{{item.name}}
        </div>
      </div>
    </div>
    <div style="position:relative;top:280px">
      <div>
        <el-button @click="rePlay" style="margin:10px 0">重来</el-button>
      </div>
      <span style="color:red" v-if="runComp">红方走</span>
      <span style="color:#000" v-if="!runComp">黑方走</span>
    </div>
  </div>
</template>

<script>
  import {request, request2} from "../network/request";

  export default {
    data() {
      return {
        chessBorders: [],    //所有格子
        runComp: true,     //行动方,为true时红方走
        runItem: {},     //选中将要移动的单位
        running: false,      //为true时表示已选中要移动的单位,点击规则范围内的格子进行移动
        couldMove: [],     //选中要移动的单位后计算的可移动的格子
        lastLostItem: {},     //被吃掉的棋子
        moveHistory: [],     //移动记录
        moveString: "", //移动记录字符串
        gameOver: false //游戏是否结束
      }
    },
    methods: {
      //选择棋子
      selectItem(x, y, name, color) {
        let id = y * 9 + x
        let trueSelect = false
        if (this.runComp && color === "#ff0000") {
          trueSelect = true
        }
        if (!this.runComp && color === "#000") {
          trueSelect = true
        }
        if (trueSelect && !this.gameOver) {
          this.$set(this.chessBorders[id], "BDcolor", color)
          this.runItem = {
            name: name,
            color: color,
            id: id,
            X: x,
            Y: y
          }
          this.getCouldMove(name, id, x, y)
          this.running = true;
        }
      },

      //移动到指定位置
      moveTo(x, y) {
        let id = y * 9 + x
        if (this.runItem.id !== id && this.running) {
          if (this.TestPlace(id)) {
            if (this.chessBorders[id].name) {
              this.lastLostItem = {id: id, name: this.chessBorders[id].name}
              this.moveHistory.push({move: this.runItem, lost: this.lastLostItem})
              if (this.chessBorders[id].name === '帅') {
                this.gameOver = true
              }
              if (this.chessBorders[id].name === '将') {
                this.gameOver = true
              }
            } else {
              this.moveHistory.push({move: this.runItem, to: id})
            }
            this.setItem(this.runItem.id)
            this.setItem(id, this.runItem.name, this.runItem.color)
            this.moveString = this.moveString.concat(this.runItem.X, this.runItem.Y, x, y)
            this.running = false
            this.runComp = !this.runComp
            this.couldMove = []
            if (this.gameOver) {
              let text = this.runItem.color === "#000" ? '黑方胜' : '红方胜'
              this.$confirm(text, '游戏结束', {
                confirmButtonText: '确定',
                type: 'success'
              }).then(() => {
                this.study()
              })
              // setTimeout(() => {
              //   alert(text)
              // }, 1000);
            }
          } else {
            this.$message({type: 'error', center: true, message: '移动的位置不合法'})
          }
        }
      },

      // 判断移动的位置是否符合规则
      /**
       * @return {boolean}
       */
      TestPlace(sid) {
        let n = false
        this.couldMove.forEach(item => {
          if (item.id === sid) {
            n = true
          }
        })
        return n
      },

      //计算可移动到的位置
      getCouldMove(name, id, x, y) {
        this.couldMove = [];
        if (name === "車" || name === "炮") {
          let C = (name === "車") ? 1 : 2
          this.moveTypeCP(x, y, C)
        }
        if (name === "馬") {
          this.moveTypeM(x, y, id)
        }
        if (name === "相" || name === "象") {
          this.moveTypeX(x, y, id)
        }
        if (name === "士" || name === "仕") {
          this.moveTypeS(x, y, id)
        }
        if (name === '将' || name === '帅') {
          this.moveTypeJ(x, y, id)
        }
        if (name === '兵' || name === '卒') {
          let B = (name === "兵") ? 1 : 2
          this.moveTypeB(x, y, id, B)
        }
      },

      //‘車’(C=1)和‘炮’(C=2)的可移动位置,
      moveTypeCP(x, y, C) {
        let addID = 0
        //  从选中位置向左遍历
        for (let xaxis = x-1; xaxis >= 0; xaxis--) {
          addID = y * 9 + xaxis
          if (this.chessBorders[addID].name) {
            /*
              '車',如果遍历到第一个非空位置的棋子颜色和选中棋子颜色不同,
              将该位置加入可移动列表并跳出for循环
             *
              '炮',遍历到非空位置后,继续遍历其后面的格子,遇到第一个非空位置
              的棋子颜色与选中棋子颜色不同,该位置加入可移动列表跳出for循环
            */
            if (C === 1 && this.chessBorders[addID].color !== this.runItem.color) {
              this.couldMove.push({id: addID, X: xaxis, Y: y})
              break;
            } else if (C === 2) {
              for (let extraX = xaxis-1; extraX >= 0; extraX--) {
                let extraID = y * 9 + extraX
                if (this.chessBorders[extraID].name !== undefined) {
                  if (this.chessBorders[extraID].color !== this.runItem.color) {
                    this.couldMove.push({id: extraID, X: extraX, Y: y})
                  }
                  break;
                }
              }
            }
            break;
          } else {
            this.couldMove.push({id: addID, X: xaxis, Y: y})
          }
        }
        //  从选中位置向右遍历
        for (let xaxis = x+1; xaxis <= 8; xaxis++) {
          addID = y * 9 + xaxis
          if (this.chessBorders[addID].name) {
            if (C === 1 && this.chessBorders[addID].color !== this.runItem.color) {
              this.couldMove.push({id: addID, X: xaxis, Y: y})
              break;
            } else if (C === 2) {
              for (let extraX = xaxis+1; extraX <= 8; extraX++) {
                let extraID = y * 9 + extraX
                if (this.chessBorders[extraID].name !== undefined) {
                  if (this.chessBorders[extraID].color !== this.runItem.color) {
                    this.couldMove.push({id: extraID, X: extraX, Y: y})
                  }
                  break;
                }
              }
            }
            break;
          } else {
            this.couldMove.push({id: addID, X: xaxis, Y: y})
          }
        }
        //  向上遍历
        for (let yaxis = y-1; yaxis >= 0; yaxis--) {
          addID = yaxis * 9 + x
          if (this.chessBorders[addID].name) {
            if (C === 1 && this.chessBorders[addID].color !== this.runItem.color) {
              this.couldMove.push({id: addID, X: x, Y: yaxis})
              break;
            } else if (C === 2) {
              for (let extraY = yaxis-1; extraY >= 0; extraY--) {
                let extraID = (extraY) * 9 + x
                if (this.chessBorders[extraID].name) {
                  if (this.chessBorders[extraID].color !== this.runItem.color) {
                    this.couldMove.push({id: extraID, X: x, Y: extraY})
                  }
                  break;
                }
              }
            }
            break;
          } else {
            this.couldMove.push({id: addID, X: x, Y: yaxis})
          }
        }
        //  向下遍历
        for (let yaxis = y+1; yaxis <= 9; yaxis++) {
          addID = (yaxis) * 9 + x
          if (this.chessBorders[addID].name) {
            if (C === 1 && this.chessBorders[addID].color !== this.runItem.color) {
              this.couldMove.push({id: addID, X: x, Y: yaxis})
              break;
            } else if (C === 2) {
              for (let extraY = yaxis+1; extraY <= 9; extraY++) {
                let extraID = (extraY) * 9 + x
                if (this.chessBorders[extraID].name !== undefined) {
                  if (this.chessBorders[extraID].color !== this.runItem.color) {
                    this.couldMove.push({id: extraID, X: x, Y: extraY})
                  }
                  break;
                }
              }
            }
            break;
          } else {
            this.couldMove.push({id: addID, X: x, Y: yaxis})
          }
        }
      },

      //'馬'可移动位置
      moveTypeM(x, y, id) {
        //  向左遍历
        if (x > 1 && !this.chessBorders[id - 1].name) {
          if (y > 0) {
            if (!this.chessBorders[id - 11].name || this.chessBorders[id - 11].color !== this.runItem.color) {
              this.couldMove.push({id: id - 11, X: x - 2, Y: y - 1})
            }
          }
          if (y < 9) {
            if (!this.chessBorders[id + 7].name || this.chessBorders[id + 7].color !== this.runItem.color) {
              this.couldMove.push({id: id + 7, X: x - 2, Y: y + 1})
            }
          }
        }
        //  向右遍历
        if (x < 7 && !this.chessBorders[id+1].name) {
          if (y > 0) {
            if (!this.chessBorders[id - 7].name || this.chessBorders[id - 7].color !== this.runItem.color) {
              this.couldMove.push({id: id - 7, X: x + 2, Y: y - 1})
            }
          }
          if (y < 9) {
            if (!this.chessBorders[id + 11].name || this.chessBorders[id + 11].color !== this.runItem.color) {
              this.couldMove.push({id: id + 11, X: x + 2, Y: y + 1})
            }
          }
        }
        //  向上遍历
        if (y > 1 && !this.chessBorders[id - 9].name) {
          if (x > 0) {
            if (!this.chessBorders[id - 19].name || this.chessBorders[id - 19].color !== this.runItem.color) {
              this.couldMove.push({id: id - 19, X: x - 1, Y: y - 2})
            }
          }
          if (x < 8) {
            if (!this.chessBorders[id - 17].name || this.chessBorders[id - 17].color !== this.runItem.color) {
              this.couldMove.push({id: id - 17, X: x + 1, Y: y - 2})
            }
          }
        }
        //  向下遍历
        if (y < 9 && !this.chessBorders[id + 9].name) {
          if (x > 0) {
            if (!this.chessBorders[id + 17].name || this.chessBorders[id + 17].color !== this.runItem.color) {
              this.couldMove.push({id: id + 17, X: x - 1, Y: y + 2})
            }
          }
          if (x < 8) {
            if (!this.chessBorders[id + 19].name || this.chessBorders[id + 19].color !== this.runItem.color) {
              this.couldMove.push({id: id + 19, X: x + 1, Y: y + 2})
            }
          }
        }
      },

      //'相'可移动位置
      moveTypeX(x, y, id) {
        if (x === 4) {
          //左下位置
          if (!this.chessBorders[id + 8].name && (!this.chessBorders[id + 16].name || this.chessBorders[id + 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 16, X: x - 2, Y: y + 2})
          }
          //右下位置
          if (!this.chessBorders[id + 10].name && (!this.chessBorders[id + 20].name || this.chessBorders[id + 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 20, X: x + 2, Y: y + 2})
          }
          //左上位置
          if (!this.chessBorders[id - 10].name && (!this.chessBorders[id - 20].name || this.chessBorders[id - 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 20, X: x - 2, Y: y - 2})
          }
          //右上位置
          if (!this.chessBorders[id - 8].name && (!this.chessBorders[id - 16].name || this.chessBorders[id - 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 16, X: x + 2, Y: y - 2})
          }
        }
        if (y === 0 || y === 5) {
          if (!this.chessBorders[id + 8].name && (!this.chessBorders[id + 16].name || this.chessBorders[id + 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 16, X: x - 2, Y: y + 2})
          }
          if (!this.chessBorders[id + 10].name && (!this.chessBorders[id + 20].name || this.chessBorders[id + 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 20, X: x + 2, Y: y + 2})
          }
        }
        if (y === 4 || y === 9) {
          if (!this.chessBorders[id - 10].name && (!this.chessBorders[id - 20].name || this.chessBorders[id - 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 20, X: x - 2, Y: y - 2})
          }
          if (!this.chessBorders[id - 8].name && (!this.chessBorders[id - 16].name || this.chessBorders[id - 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 16, X: x + 2, Y: y - 2})
          }
        }
        if (x === 0) {
          if (!this.chessBorders[id - 8].name && (!this.chessBorders[id - 16].name || this.chessBorders[id - 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 16, X: x + 2, Y: y - 2})
          }
          if (!this.chessBorders[id + 10].name && (!this.chessBorders[id + 20].name || this.chessBorders[id + 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 20, X: x + 2, Y: y + 2})
          }
        }
        if (x === 8) {
          if (!this.chessBorders[id - 10].name && (!this.chessBorders[id - 20].name || this.chessBorders[id - 20].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 20, X: x - 2, Y: y - 2})
          }
          if (!this.chessBorders[id + 8].name && (!this.chessBorders[id + 16].name || this.chessBorders[id + 16].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 16, X: x - 2, Y: y + 2})
          }
        }
      },

      //'士'可移动位置
      moveTypeS(x, y, id) {
        if (id === 3 || id === 5 || id === 21 || id === 23) {   //红色方边角位置
          if (!this.chessBorders[13].name || this.chessBorders[13].color !== this.runItem.color) {
            this.couldMove.push({id: 13, X: 4, Y: 1})
          }
        }
        if (id === 84 || id === 86 || id === 66 || id === 68) {   //黑色方边角位置
          if (!this.chessBorders[76].name || this.chessBorders[76].color !== this.runItem.color) {
            this.couldMove.push({id: 76, X: 4, Y: 8})
          }
        }
        if (id === 13 || id === 76) {   //中间位置
          if (!this.chessBorders[id - 10].name || this.chessBorders[id - 10].color !== this.runItem.color) {
            this.couldMove.push({id: id - 10, X: x - 1, Y: y - 1})
          }
          if (!this.chessBorders[id - 8].name || this.chessBorders[id - 8].color !== this.runItem.color) {
            this.couldMove.push({id: id - 8, X: x + 1, Y: y - 1})
          }
          if (!this.chessBorders[id + 8].name || this.chessBorders[id + 8].color !== this.runItem.color) {
            this.couldMove.push({id: id + 8, X: x - 1, Y: y + 1})
          }
          if (!this.chessBorders[id + 10].name || this.chessBorders[id + 10].color !== this.runItem.color) {
            this.couldMove.push({id: id + 10, X: x + 1, Y: y + 1})
          }
        }
      },

      //'将'可移动位置
      moveTypeJ(x, y, id) {
        //向左检测
        if (x - 1 >= 3 && (!this.chessBorders[id - 1].name || this.chessBorders[id - 1].color !== this.runItem.color)) {
          this.couldMove.push({id: id - 1, X: x - 1, Y: y})
        }
        //向右检测
        if (x + 1 <= 5 && (!this.chessBorders[id+1].name || this.chessBorders[id+1].color !== this.runItem.color)) {
          this.couldMove.push({id: id + 1, X: x + 1, Y: y})
        }
        //向上检测
        if ((y === 1 || y === 2 || y === 9 || y === 8) && (!this.chessBorders[id - 9].name || this.chessBorders[id - 9].color !== this.runItem.color)) {
          this.couldMove.push({id: id - 9, X: x, Y: y - 1})
        }
        //向下检测
        if ((y === 0 || y === 1 || y === 7 || y === 8) && (!this.chessBorders[id + 9].name || this.chessBorders[id + 9].color !== this.runItem.color)) {
          this.couldMove.push({id: id + 9, X: x, Y: y + 1})
        }
        //特殊规则, 将帅相吃
        if (y < 3) {
          let i = id + 9, newY = y + 1
          for (i; i < 89;) {
            if (this.chessBorders[i].name) {
              if (this.chessBorders[i].name === '帅') {
                this.couldMove.push({id: i, X: x, Y: newY})
              }
              break
            }
            i += 9
            newY++
          }
        }
        if (y > 6) {
          let i = id - 9, newY = y - 1
          for (i; i > 0;) {
            if (this.chessBorders[i].name) {
              if (this.chessBorders[i].name === '将') {
                this.couldMove.push({id: i, X: x, Y: newY})
              }
              break
            }
            i -= 9
            newY--
          }
        }
      },

      //'兵'可移动位置,红色方B=1,黑色方B=2
      moveTypeB(x, y, id, B) {
        if (B === 2) {
          //过河前
          if (y < 5 && (!this.chessBorders[id + 9].name || this.chessBorders[id + 9].color !== this.runItem.color)) {
            this.couldMove.push({id: id + 9, X: x, Y: y + 1})
          }
          //过河后
          if (y >= 5) {
            //向左检测
            if (x > 0 && (!this.chessBorders[id - 1].name || this.chessBorders[id - 1].color !== this.runItem.color)) {
              this.couldMove.push({id: id - 1, X: x - 1, Y: y})
            }
            //向右检测
            if (x < 8 && (!this.chessBorders[id+1].name || this.chessBorders[id+1].color !== this.runItem.color)) {
              this.couldMove.push({id: id + 1, X: x + 1, Y: y})
            }
            //向下检测
            if (y < 9 && (!this.chessBorders[id + 9].name || this.chessBorders[id + 9].color !== this.runItem.color)) {
              this.couldMove.push({id: id + 9, X: x, Y: y + 1})
            }
          }
        }
        if (B === 1) {
          //过河前
          if (y >= 5 && (!this.chessBorders[id - 9].name || this.chessBorders[id - 9].color !== this.runItem.color)) {
            this.couldMove.push({id: id - 9, X: x, Y: y - 1})
          }
          //过河后
          if (y < 5) {
            //向左检测
            if (x > 0 && (!this.chessBorders[id - 1].name || this.chessBorders[id - 1].color !== this.runItem.color)) {
              this.couldMove.push({id: id - 1, X: x - 1, Y: y})
            }
            //向右检测
            if (x < 8 && (!this.chessBorders[id+1].name || this.chessBorders[id+1].color !== this.runItem.color)) {
              this.couldMove.push({id: id + 1, X: x + 1, Y: y})
            }
            //向上检测
            if (y > 0 && (!this.chessBorders[id - 9].name || this.chessBorders[id - 9].color !== this.runItem.color)) {
              this.couldMove.push({id: id - 9, X: x, Y: y - 1})
            }
          }
        }
      },

      //设置格子内的棋子
      setItem(id, name, color) {
        let c;
        if (color === 1 || color === "#ff0000") {
          c = "#ff0000"
        } else {
          c = "#000"
        }
        this.$set(this.chessBorders[id], "name", name)
        this.$set(this.chessBorders[id], "color", c)
      },

      init() {   //初始化
        this.chessBorders = []
        for (let y = 0; y <= 9; y++) {
          for (let x = 0; x <= 8; x++) {
            this.chessBorders.push({X: x, Y: y})
          }
        }
        this.gameOver = false
        this.setItem(0, "車");
        this.setItem(1, "馬");
        this.setItem(2, "象");
        this.setItem(3, "士");
        this.setItem(4, "将");
        this.setItem(5, "士");
        this.setItem(6, "象");
        this.setItem(7, "馬");
        this.setItem(8, "車");
        this.setItem(19, "炮");
        this.setItem(25, "炮");
        this.setItem(27, "卒");
        this.setItem(29, "卒");
        this.setItem(31, "卒");
        this.setItem(33, "卒");
        this.setItem(35, "卒");
        this.setItem(54, "兵", 1);
        this.setItem(56, "兵", 1);
        this.setItem(58, "兵", 1);
        this.setItem(60, "兵", 1);
        this.setItem(62, "兵", 1);
        this.setItem(64, "炮", 1);
        this.setItem(70, "炮", 1);
        this.setItem(81, "車", 1);
        this.setItem(82, "馬", 1);
        this.setItem(83, "相", 1);
        this.setItem(84, "仕", 1);
        this.setItem(85, "帅", 1);
        this.setItem(86, "仕", 1);
        this.setItem(87, "相", 1);
        this.setItem(88, "馬", 1);
        this.setItem(89, "車", 1);
        this.moveHistory = []
        this.runComp = true
      },

      rePlay() {
        this.$confirm('是否确定重新开始游戏?', '重来', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.moveString = ""
          this.init()
        })
      },

      study() {
        request2({
          url: "/end/",
          method: "post",
          params: {
            s: this.moveString
          }
        }).then(res => {
          if (res.data.code === 200){
            this.$message({type: 'success', center: true, message: '人工智能已学习该棋盘'})
          }
        }).catch(res => {
          console.log("学习失败啦");
        }).finally(res => {
          this.moveString = ""
        })
      }
    },
    mounted() {
      this.init()
    },
    watch: {
      runItem: function (newItem, oldItem) {    //重新选择棋子时,去掉上一个选中的边框颜色
        if (oldItem.id && oldItem.id !== newItem.id) {
          let c = '#aaaaaa'
          let id = oldItem.id
          this.$set(this.chessBorders[id], "BDcolor", c)
        }
      },
      runComp: function (newColor, oldColor) {
        //从红方行动变为黑方行动且游戏未结束时，触发人工智能走棋
        if (!newColor && oldColor && !this.gameOver){
          request({
            url: '/next/',
            method: 'post',
            params: {
              s: this.moveString
            },
          }).then(res => {
            console.log(res);
            if (res.data.data !== ''){
              console.log("进行");
              let xa = parseInt(res.data.data.substr(0, 1))
              let ya = parseInt(res.data.data.substr(1, 1))
              let xb = parseInt(res.data.data.substr(2, 1))
              let yb = parseInt(res.data.data.substr(3, 1))
              let ch = this.chessBorders[ya*9+xa];
              this.selectItem(ch.X, ch.Y, ch.name, ch.color)
              this.moveTo(xb, yb);
            }else {
              this.$message({type: 'info', center: true, message: "人工智能不会走棋啦"})
            }
          }).catch(res => {
            this.$message({type: 'info', center: true, message: "人工智能不会走棋啦"})
            console.log("人工智能请求失败");
            //this.$message({type: 'error', center: true, message: "人工智能请求失败"})
          })
        }
      }
    }
  }
</script>

<style scoped>
  .box {
    width: 540px;
    height: 600px;
    border: 1px solid #cccccc;
    position: absolute;
    z-index: 2;
  }

  .chessborder {
    width: 60px;
    height: 60px;
    float: left;
  }

  .chooseman {
    background-color: #333;
    border-color: #aaaaaa
  }

  .chessman {
    width: 50px;
    height: 50px;
    line-height: 50px;
    text-align: center;
    font-size: 35px;
    font-weight: bolder;
    font-family: "KaiTi";
    background-color: #eeeeee;
    border: 3px solid #bbbbbb;
    position: relative;
    top: 2px;
    left: 2px;
    border-radius: 50%;
  }
</style>
