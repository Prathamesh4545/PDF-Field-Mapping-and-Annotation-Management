import { useState } from 'react'
import PDFMapper from './PDFMapper'
import ExecutivePage from './ExecutivePage'
import './App.css'

function App() {
  const [currentPage, setCurrentPage] = useState('mapping')

  return (
    <>
      <nav className="app-nav">
        <button 
          className={currentPage === 'mapping' ? 'active' : ''}
          onClick={() => setCurrentPage('mapping')}
        >
          Mapping Page
        </button>
        <button 
          className={currentPage === 'executive' ? 'active' : ''}
          onClick={() => setCurrentPage('executive')}
        >
          Executive Page
        </button>
      </nav>
      
      {currentPage === 'mapping' && <PDFMapper pdfUrl="/sample.pdf" />}
      {currentPage === 'executive' && <ExecutivePage />}
    </>
  )
}

export default App
