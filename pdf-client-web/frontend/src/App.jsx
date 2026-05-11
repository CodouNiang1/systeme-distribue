import { useState } from 'react'
import * as api from './api'

const services = [
  'Fusionner','Découper','Extraire page',
  'Supprimer page','Mot de passe',
  'PDF → Image','Extraire texte','Créer PDF'
]

const FileInput = ({ label, onChange }) => (
  <label style={styles.fileLabel}>
    📂 {label}
    <input type="file" accept=".pdf" onChange={e => onChange(e.target.files[0])}
      style={{ display: 'none' }} />
  </label>
)

const NumInput = ({ placeholder, value, onChange }) => (
  <input type="number" placeholder={placeholder} value={value}
    onChange={e => onChange(e.target.value)} style={styles.input} min="1" />
)

export default function App() {
  const [tab, setTab] = useState(0)
  const [status, setStatus] = useState('')
  const [texte, setTexte] = useState('')
  const [loading, setLoading] = useState(false)

  // États des formulaires
  const [pdf1, setPdf1] = useState(null)
  const [pdf2, setPdf2] = useState(null)
  const [pdf, setPdf] = useState(null)
  const [debut, setDebut] = useState('')
  const [fin, setFin] = useState('')
  const [page, setPage] = useState('')
  const [mdp, setMdp] = useState('')
  const [contenu, setContenu] = useState('')

  const run = async (fn) => {
    setLoading(true); setStatus(''); setTexte('')
    try {
      const result = await fn()
      if (typeof result === 'string') setTexte(result)
      setStatus('✅ Opération réussie !')
    } catch (e) {
      setStatus('❌ Erreur : ' + (e.response?.data || e.message))
    } finally { setLoading(false) }
  }

  const panels = [
    // 0 - Fusionner
    <div style={styles.panel}>
      <p style={styles.desc}>Fusionne deux fichiers PDF en un seul.</p>
      <FileInput label={pdf1 ? pdf1.name : 'Sélectionner PDF 1'} onChange={setPdf1} />
      <FileInput label={pdf2 ? pdf2.name : 'Sélectionner PDF 2'} onChange={setPdf2} />
      <button style={styles.btn} onClick={() => run(() => api.fusionner(pdf1, pdf2))}>
        Fusionner
      </button>
    </div>,

    // 1 - Découper
    <div style={styles.panel}>
      <p style={styles.desc}>Extrait un intervalle de pages du PDF.</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <div style={styles.row}>
        <NumInput placeholder="Page début" value={debut} onChange={setDebut} />
        <NumInput placeholder="Page fin" value={fin} onChange={setFin} />
      </div>
      <button style={styles.btn}
        onClick={() => run(() => api.decouper(pdf, debut, fin))}>
        Découper
      </button>
    </div>,

    // 2 - Extraire page
    <div style={styles.panel}>
      <p style={styles.desc}>Extrait une seule page du PDF.</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <NumInput placeholder="Numéro de page" value={page} onChange={setPage} />
      <button style={styles.btn}
        onClick={() => run(() => api.extrairePage(pdf, page))}>
        Extraire
      </button>
    </div>,

    // 3 - Supprimer page
    <div style={styles.panel}>
      <p style={styles.desc}>Supprime une page du PDF.</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <NumInput placeholder="Page à supprimer" value={page} onChange={setPage} />
      <button style={styles.btn}
        onClick={() => run(() => api.supprimerPage(pdf, page))}>
        Supprimer
      </button>
    </div>,

    // 4 - Mot de passe
    <div style={styles.panel}>
      <p style={styles.desc}>Chiffre le PDF avec un mot de passe (AES-128).</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <input type="password" placeholder="Mot de passe" value={mdp}
        onChange={e => setMdp(e.target.value)} style={styles.input} />
      <button style={styles.btn}
        onClick={() => run(() => api.ajouterMotDePasse(pdf, mdp))}>
        Protéger
      </button>
    </div>,

    // 5 - PDF → Image
    <div style={styles.panel}>
      <p style={styles.desc}>Convertit une page PDF en image PNG (150 DPI).</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <NumInput placeholder="Numéro de page" value={page} onChange={setPage} />
      <button style={styles.btn}
        onClick={() => run(() => api.convertirImage(pdf, page))}>
        Convertir
      </button>
    </div>,

    // 6 - Extraire texte
    <div style={styles.panel}>
      <p style={styles.desc}>Extrait tout le texte du PDF.</p>
      <FileInput label={pdf ? pdf.name : 'Sélectionner PDF'} onChange={setPdf} />
      <button style={styles.btn}
        onClick={() => run(() => api.extraireTexte(pdf))}>
        Extraire le texte
      </button>
      {texte && <textarea value={texte} readOnly style={styles.textarea} />}
    </div>,

    // 7 - Créer PDF
    <div style={styles.panel}>
      <p style={styles.desc}>Crée un nouveau PDF depuis un texte.</p>
      <textarea placeholder="Tapez votre contenu ici..."
        value={contenu} onChange={e => setContenu(e.target.value)}
        style={{ ...styles.textarea, height: 200 }} />
      <button style={styles.btn}
        onClick={() => run(() => api.creerPDF(contenu))}>
        Créer le PDF
      </button>
    </div>,
  ]

  return (
    <div style={styles.root}>
      {/* Header */}
      <div style={styles.header}>
        <h1 style={styles.title}>🗂 Client PDF — CORBA</h1>
        <p style={styles.subtitle}>Gestion de documents PDF via serveur CORBA</p>
      </div>

      {/* Tabs */}
      <div style={styles.tabs}>
        {services.map((s, i) => (
          <button key={i} onClick={() => { setTab(i); setStatus('') }}
            style={{ ...styles.tab, ...(tab === i ? styles.tabActive : {}) }}>
            {s}
          </button>
        ))}
      </div>

      {/* Content */}
      <div style={styles.content}>
        {loading
          ? <div style={styles.loading}>⏳ Traitement en cours...</div>
          : panels[tab]
        }
        {status && (
          <div style={{
            ...styles.status,
            background: status.startsWith('✅') ? '#E8F5E9' : '#FFEBEE',
            color: status.startsWith('✅') ? '#2E7D32' : '#C62828',
          }}>
            {status}
          </div>
        )}
      </div>
    </div>
  )
}

const styles = {
  root: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #F0F4FF 0%, #E8EEFF 100%)',
    fontFamily: "'Segoe UI', sans-serif",
  },
  header: {
    background: 'linear-gradient(135deg, #2D3A8C, #4F6BED)',
    padding: '32px 40px 24px',
    color: 'white',
  },
  title: { margin: 0, fontSize: 28, fontWeight: 700 },
  subtitle: { margin: '8px 0 0', opacity: 0.8, fontSize: 14 },
  tabs: {
    display: 'flex', flexWrap: 'wrap', gap: 4,
    padding: '16px 40px 0',
    background: '#E8EEFF',
  },
  tab: {
    padding: '10px 18px', border: 'none', borderRadius: '8px 8px 0 0',
    cursor: 'pointer', fontSize: 13, fontWeight: 600,
    background: '#D4DCF8', color: '#4A5580', transition: 'all 0.2s',
  },
  tabActive: {
    background: 'white', color: '#2D3A8C',
    boxShadow: '0 -2px 8px rgba(45,58,140,0.1)',
  },
  content: {
    background: 'white', margin: '0 40px 40px',
    borderRadius: '0 12px 12px 12px',
    padding: '32px', minHeight: 300,
    boxShadow: '0 4px 20px rgba(45,58,140,0.1)',
  },
  panel: { display: 'flex', flexDirection: 'column', gap: 16, maxWidth: 480 },
  desc: { color: '#5A6A8A', fontSize: 14, margin: 0 },
  fileLabel: {
    display: 'inline-block', padding: '10px 20px',
    background: '#F0F4FF', border: '2px dashed #C0CAEE',
    borderRadius: 8, cursor: 'pointer', fontSize: 14,
    color: '#4A5580', transition: 'all 0.2s',
  },
  input: {
    padding: '10px 14px', border: '1.5px solid #C0CAEE',
    borderRadius: 8, fontSize: 14, color: '#2D3748',
    outline: 'none', maxWidth: 200,
  },
  row: { display: 'flex', gap: 12 },
  btn: {
    padding: '12px 28px', border: 'none', borderRadius: 10,
    background: 'linear-gradient(135deg, #4F6BED, #2D3A8C)',
    color: 'white', fontWeight: 700, fontSize: 14,
    cursor: 'pointer', alignSelf: 'flex-start',
    boxShadow: '0 4px 12px rgba(45,58,140,0.3)',
  },
  textarea: {
    width: '100%', padding: '12px', border: '1.5px solid #C0CAEE',
    borderRadius: 8, fontSize: 13, resize: 'vertical',
    minHeight: 120, color: '#2D3748', boxSizing: 'border-box',
  },
  status: {
    marginTop: 20, padding: '12px 16px',
    borderRadius: 8, fontSize: 14, fontWeight: 600,
  },
  loading: { textAlign: 'center', padding: 40, fontSize: 16, color: '#4A5580' },
}
