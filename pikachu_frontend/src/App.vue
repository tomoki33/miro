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
              fontSize: note.fontSize + 'px'
              }"
            @focus="highlightText(); editingIndex = index"
            @click="selectNote(index)"
            @blur="editingIndex = -1; adjustFontSize(index)"
          ></textarea>
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
      noteColor: '',
      penColor: '#000000',
      dragStartX: 0,
      dragStartY: 0,
      highlighted: false,
      selectedNoteIndex: -1,
      showColorPalette: false,
      showPenColorPalette: false,
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
    highlightText() {
      // フォーカス時の処理
      this.highlighted = true;
    },
    getpikachu(){
      Service.get('/getpikachu').then(response => {
        console.log('帰って来たGET:',response.data)
        this.notes = response.data
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
      };
      this.notes.push(newNote);
    },
    startDragging(index, event) {
      this.draggingIndex = index;
      this.dragStartX = event.clientX - this.notes[index].x;
      this.dragStartY = event.clientY - this.notes[index].y;

      window.addEventListener("mousemove", this.dragNote);
      window.addEventListener("mouseup", this.stopDragging);
    },
    selectNote(index) {
    this.selectedNoteIndex = index;
  },
    changePenColor() {
      if (this.selectedNoteIndex >= 0) {
      this.notes[this.selectedNoteIndex].textColor = this.penColor;
      }
    },
    dragNote(event) {
      if (this.draggingIndex >= 0) {
        this.notes[this.draggingIndex].x = Math.max(event.clientX - this.dragStartX,0)
        this.notes[this.draggingIndex].y = Math.max(event.clientY - this.dragStartY,0)
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
  width: 200px;
  height: auto;
  padding: 8px;
  cursor: move;
}

.note-content {
  position: relative;
}

.note-content textarea {
  width: 100%;
  height: 100%;
  border: none;
  resize: none;
  outline: none;
  font-size: 14px;
  line-height: 1.5;
  resize: both; /*テキストエリアのサイズを拡大縮小可能にする */
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

textarea:focus {
  border: 2px solid #1e2226; /* ハイライトの境界線の色を指定 */
  /* その他のスタイルを追加できます（例：背景色、テキスト色など） */
}
.btn {
  background-color:rgba(0, 0, 0, 0);
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
  width: 136px; /* 2列の幅を設定します（各色オプションが60pxの場合） */
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
</style>