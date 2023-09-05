<!-- <template>
  <div>
    <input type="text" v-model="inputText" placeholder="Enter text">
    <input type="color" v-model="noteColor">
    <button @click="addNote">Add Note</button>

  <div class="canvas-container">
    <canvas
      ref="canvas"
      :width="canvasWidth"
      :height="canvasHeight"
      @click="handleClick"
    ></canvas>
    <textarea ref="textarea" class="overlay-textarea"></textarea>
  </div>
  </div>
</template> -->
<template>
  <div>
    <input type="text" v-model="inputText" placeholder="Enter text">
    <input type="color" v-model="noteColor">
    <button @click="addNote">Add Note</button>

    <div class="canvas-container">
      <canvas
        ref="canvas"
        :width="canvasWidth"
        :height="canvasHeight"
        @click="handleClick"
      ></canvas>
      <!-- <textarea ref="textarea" class="overlay-textarea"></textarea> -->
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      notes: [],
      draggingNote: null,
      resizingNote: null,
      editingNote: null, // 編集中の付箋を追加
      inputText: '',
      noteColor: '#FFFF00',
      canvasWidth: 1800,
      canvasHeight: 1600,
      resizeHandleSize: 20,
    };
  },
  mounted() {
    this.loadNotes();
    this.initializeCanvas();
  },
  methods: {
    initializeCanvas() {
      const canvas = this.$refs.canvas;
      const ctx = canvas.getContext('2d');
      canvas.addEventListener('mousedown', this.handleMouseDown);
      canvas.addEventListener('mousemove', this.handleMouseMove);
      canvas.addEventListener('mouseup', this.handleMouseUp);
      this.ctx = ctx;
      this.drawNotes();
    },
    drawNotes() {
      const ctx = this.ctx;
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
      this.notes.forEach((note) => {
        ctx.fillStyle = note.color;
        ctx.fillRect(note.x, note.y, note.width, note.height);
        ctx.fillStyle = '#000';
        ctx.font = '14px Arial';
        ctx.fillText(note.text, note.x + 10, note.y + 30);
        // ×ボタンを描画
        ctx.fillStyle = '#FF0000';
        ctx.font = '18px Arial';
        ctx.fillText('×', note.x + note.width - 20, note.y + 25);
        // リサイズハンドルを描画
        ctx.fillStyle = '#0000FF';
        ctx.fillRect(
          note.x + note.width - this.resizeHandleSize,
          note.y + note.height - this.resizeHandleSize,
          this.resizeHandleSize,
          this.resizeHandleSize
        );
        // 編集中の付箋の囲いを描画
        if (this.editingNote && this.editingNote === note) {
          ctx.strokeStyle = '#FF0000';
          ctx.lineWidth = 2;
          ctx.strokeRect(note.x, note.y, note.width, note.height);
        }
      });
    },
    saveNotes() {
      localStorage.setItem('notes', JSON.stringify(this.notes));
    },
    loadNotes() {
      const savedNotes = localStorage.getItem('notes');
      if (savedNotes) {
        this.notes = JSON.parse(savedNotes);
      }
    },
    addNote() {
      const note = {
        x: 50,
        y: 50,
        width: 150,
        height: 150,
        text: this.inputText,
        color: this.noteColor,
        editable: false,
      };
      this.notes.push(note);
      this.drawNotes();
      this.saveNotes();
      this.inputText = '';
    },
    removeNote(index) {
      this.notes.splice(index, 1);
      this.drawNotes();
      this.saveNotes();
    },
    getCursorPosition(event) {
      const rect = this.$refs.canvas.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;
      return { x, y };
    },
    findNoteAtPosition(x, y) {
      return this.notes.find(
        (note) =>
          x >= note.x &&
          x <= note.x + note.width &&
          y >= note.y &&
          y <= note.y + note.height
      );
    },
    handleMouseDown(event) {
      const { x, y } = this.getCursorPosition(event);
      const clickedNote = this.findNoteAtPosition(x, y);
      if (clickedNote) {
        if (
          x >= clickedNote.x + clickedNote.width - this.resizeHandleSize &&
          y >= clickedNote.y + clickedNote.height - this.resizeHandleSize
        ) {
          // クリックされた位置がリサイズハンドルの範囲内ならリサイズ処理
          this.resizingNote = clickedNote;
          this.dragOffsetX = clickedNote.width - x;
          this.dragOffsetY = clickedNote.height - y;
        } else {
          // クリックされた位置がノートの範囲内ならドラッグ処理
          this.draggingNote = clickedNote;
          this.dragOffsetX = x - clickedNote.x;
          this.dragOffsetY = y - clickedNote.y;
          // クリックされた付箋を編集中にする
          this.editingNote = clickedNote;
        }
      }
    },
    handleMouseMove(event) {
      if (this.draggingNote) {
        const { x, y } = this.getCursorPosition(event);
        this.draggingNote.x = x - this.dragOffsetX;
        this.draggingNote.y = y - this.dragOffsetY;
        this.drawNotes();
        this.saveNotes();
      } else if (this.resizingNote) {
        const { x, y } = this.getCursorPosition(event);
        const newWidth = x - this.resizingNote.x + this.dragOffsetX;
        const newHeight = y - this.resizingNote.y + this.dragOffsetY;
        this.resizingNote.width = Math.max(newWidth, this.resizeHandleSize);
        this.resizingNote.height = Math.max(newHeight, this.resizeHandleSize);
        this.drawNotes();
        this.saveNotes();
      }
    },
    handleMouseUp() {
      this.draggingNote = null;
      this.resizingNote = null;
    },
    handleClick(event) {
      const { x, y } = this.getCursorPosition(event);
      const clickedNote = this.findNoteAtPosition(x, y);
      if (
        clickedNote &&
        x >= clickedNote.x + clickedNote.width - 20 &&
        x <= clickedNote.x + clickedNote.width &&
        y >= clickedNote.y + 10 &&
        y <= clickedNote.y + 30
      ) {
        // クリックされた位置が削除ボタンの範囲内なら削除処理
        this.removeNote(this.notes.indexOf(clickedNote));
      } else if (clickedNote && this.editingNote !== clickedNote) {
        // クリックされた位置が付箋内で、編集中の付箋でない場合は編集モードに切り替える
        this.editingNote = clickedNote;
      } else if (this.editingNote && this.editingNote !== clickedNote) {
        // クリックされた位置が付箋外で、編集中の付箋がある場合は編集モードを終了する
        this.editingNote = null;
      }
    },
  },
};
</script>

<style>
.canvas-container {
  position: relative;
  width: fit-content;
  height: fit-content;
}

.overlay-textarea {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 60%;
  opacity: 0.8;
  background-color: white;
  border: none;
  resize: none;
}
</style>
