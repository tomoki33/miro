<template>
  <div>
    <input type="color" v-model="noteColor">
    <button @click="addNote">Add Note</button>
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
            :style="{ backgroundColor: note.color , textAlign: 'center', fontSize: note.fontSize + 'px' }"
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
import { Service } from './service/service'

export default {
  data() {
    return {
      notes: [],
      editingIndex: -1,
      draggingIndex: -1,
      noteColor: '#FFFF00',
      dragStartX: 0,
      dragStartY: 0
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
    setInterval(() => {
      this.getpikachu();
    }, 1000);
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
        fontSize: 14
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
      const noteTextArea = this.$refs.noteTextArea[index];
      const noteContent = noteTextArea.parentNode;
      const noteHeight = noteContent.clientHeight;
      const noteWidth = noteContent.clientWidth;
      const fontSize = Math.min(noteHeight, noteWidth) / 2;
      this.notes[index].fontSize = fontSize;
    }
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
  aspect-ratio: 1/1; /* add this line to make the note square */
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
</style>