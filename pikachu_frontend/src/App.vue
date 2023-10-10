<template>
  <!-- <input type="color" v-model="noteColor"> -->
  <div>
    <input type="color" style="margin-right: 10px;" v-model="noteColor">
    <button class="btn" @click="addNote" title="付箋を追加">
      <i class="fa-regular fa-note-sticky"></i></button>
    <button class="btn" @click="penColor" title="文字の色を変更">
      <i class="fa-solid fa-pen"></i></button>
    <!-- <button @click="toggleColorMenu">NoteColor</button> -->
    <ul v-if="showColorMenu" class="color-menu">
      <li
        v-for="(colorOption, index) in noteColors"
        :key="index"
        @click="changeNoteColor(colorOption.value)"
      >{{ colorOption.name }}</li>
    </ul>
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
            @click="selectNote(index)"
            @focus="editingIndex = index"
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
      noteColor: '#FFFF00',
      dragStartX: 0,
      dragStartY: 0,
      // textColor: '#000000' ,//初期の文字カラー
      noteColors: [
      { name: 'Black', value: '#000000' },
      { name: 'Red', value: '#FF0000' },
      { name: 'Green', value: '#00FF00' },
      { name: 'Blue', value: '#0000FF' },
      // 他の色オプションを追加できます
    ],
    showColorMenu: false,
    selectedNoteIndex: -1
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
    penColor() {
      if (this.selectedNoteIndex >= 0) {
      this.notes[this.selectedNoteIndex].textColor = this.noteColor;
      }
    },
    dragNote(event) {
      if (this.draggingIndex >= 0) {
        this.notes[this.draggingIndex].x = event.clientX - this.dragStartX;
        this.notes[this.draggingIndex].y = event.clientY - this.dragStartY;
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
    toggleColorMenu() {
    this.showColorMenu = !this.showColorMenu;
    },
    changeNoteColor(color) {
      this.noteColor = color;
      this.showColorMenu = false; // メニューを閉じる
      this.notes.forEach((note) => {
        note.color = this.noteColor;
      });
    },
  }
};
</script>

<style scoped>
.canvas {
  position: relative;
  width: 10000px;
  height: 10000px;
  background-color: #82c4e8;
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
.btn {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  font-size: 16px;
  padding: 10px 20px;
  margin-bottom: 10px;
  margin-right: 5px;
}
</style>