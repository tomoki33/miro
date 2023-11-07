<template>
  <div>
    <div class="menu">
      <button class="btn" @click="toggleColorPalette" title="付箋を追加">
        <i class="fa-regular fa-note-sticky"></i></button>
        <div v-if="showColorPalette" class="color-palette">
          <div
            v-for="(colorOption, index) in noteColors"
            :key="index"
            class="color-option"
            :style="{ backgroundColor: colorOption.value }"
            @click="selectNoteColor(colorOption.value)"
          ></div>
          <div class="color-option-1">？</div>
        </div>

      <button class="btn" @click="togglePenColorPalette" title="文字の色を変更">
        <i class="fa-solid fa-pen"></i></button>
        <div v-if="showPenColorPalette" class="color-palette">
          <div
            v-for="(colorOption, index) in penColors"
            :key="index"
            class="pen-color-option"
            :style="{ backgroundColor: colorOption.value }"
            @click="selectPenColor(colorOption.value)"
          ></div>
        </div>
    </div>
    <div class="canvas">
      <div
        v-for="(note, index) in notes"
        :key="index"
        class="sticky-note"
        :style="{ left: note.x + 'px', top: note.y + 'px' }"
        @mousedown="startDragging(index, $event)"
      >
        <div class="note-content">
          <textarea
            ref="noteTextArea"
            v-model="note.text"
            :style="{
              backgroundColor: note.color ,
              textAlign: 'center',
              color: note.textColor,
              fontSize: note.fontSize + 'px',
              width: note.width + 'px',
              height: note.width + 'px'
              }"
            @focus="highlightText(); editingIndex = index"
            @click="selectNote(index)"
            @blur="editingIndex = -1; adjustFontSize(index)"
          ></textarea>
          <div class="handle" @mousedown="startResize(index,$event)"></div>
          <button class="delete-button" @click="removeNote(index)">×</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Service } from './service/service';
import '@fortawesome/fontawesome-free/css/all.css';


export default {
  data() {
    return {
      notes: [],
      editingIndex: -1,
      draggingIndex: -1,
      resizingIndex: -1,
      noteColor: '',
      penColor: '#000000',
      dragStartX: 0,
      dragStartY: 0,
      highlighted: false,
      selectedNoteIndex: -1,
      showColorPalette: false,
      showPenColorPalette: false,
      isResizing: false,
      resizeHandle: null,
      startX: 0,
      startY: 0,
      startWidth: 0,
      startHeight: 0,
      noteColors: [
        { name: "1", value: "#ffd5d5" },
        { name: "2", value: "#ffaeae" },
        { name: "3", value: "#f8d4c3" },
        { name: "4", value: "#f8eec3" },
        { name: "5", value: "#dbf8c3" },
        { name: "6", value: "#d8f2f3" },
        { name: "7", value: "#d4d7f1" },
        { name: "8", value: "#dac7e0" },
        { name: "9", value: "#b8adad" },
        { name: "10", value: "#ffffff" },
      ],
      penColors: [
        { name: "1", value: "#ff6a6a" },
        { name: "2", value: "#f1aa89" },
        { name: "3", value: "#a4d67a" },
        { name: "4", value: "#afb7ff" },
        { name: "5", value: "#af8bb9" },
        { name: "6", value: "#000000" },
        { name: "7", value: "#6e5f58" },
        { name: "8", value: "#ffffff" },
      ]
    };
  },
  watch: {
    notes: {
      handler() {
        this.pikachu()
      },
      deep: true, // ネストされたプロパティも監視
    },
  },
  created(){
    this.getpikachu();
    // setInterval(() => {
    //   this.getpikachu();
    // }, 1000);
  },
  methods: {
    startResize(index,event) {
      console.log('startResizeリサイズ');
      // リサイズ開始
      this.isResizing = true;
      this.resizingIndex = index;
      console.log('インデックス',index)
      console.log('ハンドルX',event.clientX)
      this.startX = event.clientX
      this.startY = event.clientY
      this.startWidth = this.notes[index].width;
      this.startHeight = this.notes[index].width;

      // マウスムーブとマウスアップのリスナーを追加
      window.addEventListener("mousemove", this.performResize);
      window.addEventListener("mouseup", this.stopResize);
    },
    performResize(event) {
      if (this.isResizing) {
        // リサイズ中
        const deltaX = event.clientX - this.startX;
        const deltaY = event.clientY - this.startY;

        // 新しいサイズを計算
        let newWidth = this.startWidth;
        let newHeight = this.startHeight;
        console.log('右')
        newWidth += deltaX;
        newHeight += deltaY;
        // サイズを更新
        console.log('width',newWidth);
        console.log('height',newHeight)
        this.notes[this.resizingIndex].width = Math.max(newWidth, 100); // 最小幅を設定
      }
    },
    stopResize() {
      // リサイズ終了
      this.isResizing = false;
      this.resizingIndex = -1;

      // マウスムーブとマウスアップのリスナーを削除
      window.removeEventListener("mousemove", this.performResize);
      window.removeEventListener("mouseup", this.stopResize);
    },
    highlightText() {
      // フォーカス時の処理
      this.highlighted = true;
    },
    getpikachu(){
      Service.get('/getpikachu').then(response => {
        console.log('帰って来たGET:',response.data)
        this.notes = response.data
        console.log(this.notes)
      })
    },
    pikachu(){
      console.log('pikachu:',JSON.stringify(this.notes))
    Service.post('/pikachu', JSON.stringify(this.notes))
        .then(response => {
         console.log('帰って来た:',response.data)
        })
        .catch(error => {
          console.log('えらー:',error);
        })
    },
    addNote() {
      const newNote = {
        x: 50,
        y: 50,
        color: this.noteColor,
        text: "",
        textColor:'#000000',
        width: 200,
      };
      this.notes.push(newNote);
    },
    startDragging(index, event) {
      if (this.isResizing) {
        event.stopPropagation();
      } else {
        console.log('aaaaa',getComputedStyle(document.body).cursor);
        this.draggingIndex = index;
        this.dragStartX = event.pageX - this.notes[index].x;
        this.dragStartY = event.pageY - this.notes[index].y;

        window.addEventListener("mousemove", this.dragNote);
        window.addEventListener("mouseup", this.stopDragging);
      }
    },
    selectNote(index) {
      console.log('ふせん',this.notes[index].width)
      this.selectedNoteIndex = index;
    },
    changePenColor() {
      if (this.selectedNoteIndex >= 0) {
      this.notes[this.selectedNoteIndex].textColor = this.penColor;
      }
    },
    dragNote(event) {
      if (this.draggingIndex >= 0) {
        this.notes[this.draggingIndex].x = Math.max(event.pageX - this.dragStartX, 0);
        this.notes[this.draggingIndex].y = Math.max(event.pageY - this.dragStartY, 0);
        const mouseX = event.clientX;
        const mouseY = event.clientY;

        // 画面のサイズを取得
        const windowWidth = window.innerWidth;
        const windowHeight = window.innerHeight;

        // スクロールの閾値を設定
        const scrollThreshold = 50;

        // マウスが画面の端に近づいたときにスクロールを開始
        if (mouseX < scrollThreshold) {
          window.scrollBy(-20, 0);
        } else if (windowWidth - mouseX < scrollThreshold) {
          window.scrollBy(20, 0);
        }

        if (mouseY < scrollThreshold) {
          window.scrollBy(0, -20);
        } else if (windowHeight - mouseY < scrollThreshold) {
          window.scrollBy(0, 20);
        }
      }
    },
    stopDragging() {
      this.draggingIndex = -1;

      window.removeEventListener("mousemove", this.dragNote);
      window.removeEventListener("mouseup", this.stopDragging);
    },
    removeNote(index) {
      this.notes.splice(index, 1);
    },
    adjustFontSize(index) {
      console.log("adjustFontSize");
      const noteTextArea = this.$refs.noteTextArea[index]; // テキストエリアの要素を取得
      const noteContent = noteTextArea.parentNode; // 親要素を取得
      const noteHeight = noteContent.clientHeight; // 親要素の高さを取得
      const noteWidth = noteContent.clientWidth; // 親要素の幅を取得
      const fontSize = Math.min(noteHeight, noteWidth) / 5; // 高さと幅の最小値を使って新しいフォントサイズを計算

      // ノートのフォントサイズを更新
      this.notes[index].fontSize = fontSize;
    },
    toggleColorPalette() {
      this.showPenColorPalette = false;
      this.showColorPalette = !this.showColorPalette;
    },
    togglePenColorPalette() {
      this.showColorPalette = false;
      this.showPenColorPalette = !this.showPenColorPalette;
    },
    changeNoteColor(color) {
      this.noteColor = color;
      this.showColorMenu = false; // メニューを閉じる
      this.notes.forEach((note) => {
        note.color = this.noteColor;
      });
    },
    selectNoteColor(color) {
      this.showColorPalette = false;
      this.noteColor = color;
      this.addNote();
    },
    selectPenColor(color) {
      this.showPenColorPalette = false;
      this.penColor = color;
      this.changePenColor();
    }
  }
};
</script>

<style scoped>
.canvas {
  z-index: 1;
  position: relative;
  width: 10000px;
  height: 10000px;
  background-color: #d8d1c948;
}

.sticky-note {
  position: absolute;
  padding: 8px;
}

.note-content {
  position: relative;
}

.note-content textarea {
  width: 100%;
  height: 100%;
  border: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  aspect-ratio: 1/1; /* add this line to make the note square */
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.delete-button {
  position: absolute;
  top: 0;
  right: 0;
  background-color: transparent;
  border: none;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}

textarea {
  resize: none;
}

textarea:focus {
  border: 2px solid #616161; /* ハイライトの境界線の色を指定 */
  /* その他のスタイルを追加できます（例：背景色、テキスト色など） */
}
.btn {
  background-color:#d8d1c948;
  border: 1px solid #a1a1a1;
  border-radius: 10px;
  font-size: 16px;
  padding: 10px 20px;
  margin-bottom: 10px;
  margin-right: 5px;
}
.menu {
  z-index: 2;
  position: fixed;
  top: 20px;
  left: 20px;
}

.color-palette {
  position: absolute;
  display: flex;
  flex-wrap: wrap;
  width: 137px; /* 2列の幅を設定します（各色オプションが60pxの場合） */
  background-color: white;
  border: 1px solid #ddd;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 10; /* 他の要素の上に表示するため */
  padding: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.color-option {
  width: 50px;
  height: 50px;
  cursor: pointer;
  margin: 8px; /* カラーアイテム間のスペースを追加 */
  display: block; /* テキストがなくても領域が表示されるようにする */
  border: 1px solid #ddd;
}

.pen-color-option {
  width: 50px;
  height: 50px;
  cursor: pointer;
  margin: 8px; /* カラーアイテム間のスペースを追加 */
  display: block; /* テキストがなくても領域が表示されるようにする */
  border: 1px solid #ddd;
  border-radius: 50px;
}

.color-option-1 {
  width: 45px;
  height: 45px;
  background-color: #FFD700; /* ゴールデン色 */
  border: 3px solid #000;    /* 黒のボーダー */
  font-size: 24px;           /* フォントサイズ */
  line-height: 60px;         /* インライン要素の中央寄せ */
  text-align: center;        /* テキストを中央寄せ */
  font-weight: bold;         /* 太字 */
  box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.5); /* 軽微な影 */
  font-family: "Arial", sans-serif; /* Arial フォント（他のフォントに変更可能） */
  position: relative;
  margin: 8px;      /* 以下、疑問符のドロップシャドウのための設定 */
}

.color-option-1::before {
  position: absolute;
  bottom: -5px;
  left: 2px;                 /* 少し右にずらす */
  color: rgba(0, 0, 0, 0.2); /* 影の色 */
  z-index: -1;               /* 元のテキストの背後に配置 */
}

.handle {
  width: 10px;
  height: 10px;
  background-color: rgb(100, 96, 96);/* or any color you want for the handles */
  position: absolute;
  bottom: 4px;
  right: 0;
  cursor: se-resize;
}

</style>