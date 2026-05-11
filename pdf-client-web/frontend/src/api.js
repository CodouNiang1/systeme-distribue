import axios from 'axios'

const BASE = '/api/pdf'

const dl = (data, filename) => {
  const url = URL.createObjectURL(new Blob([data]))
  const a = document.createElement('a')
  a.href = url; a.download = filename; a.click()
  URL.revokeObjectURL(url)
}

export const fusionner = async (pdf1, pdf2) => {
  const fd = new FormData()
  fd.append('pdf1', pdf1); fd.append('pdf2', pdf2)
  const r = await axios.post(`${BASE}/fusionner`, fd, { responseType: 'blob' })
  dl(r.data, 'fusion.pdf')
}

export const decouper = async (pdf, debut, fin) => {
  const fd = new FormData()
  fd.append('pdf', pdf); fd.append('debut', debut); fd.append('fin', fin)
  const r = await axios.post(`${BASE}/decouper`, fd, { responseType: 'blob' })
  dl(r.data, 'decoupage.pdf')
}

export const extrairePage = async (pdf, page) => {
  const fd = new FormData()
  fd.append('pdf', pdf); fd.append('page', page)
  const r = await axios.post(`${BASE}/extraire-page`, fd, { responseType: 'blob' })
  dl(r.data, `page_${page}.pdf`)
}

export const supprimerPage = async (pdf, page) => {
  const fd = new FormData()
  fd.append('pdf', pdf); fd.append('page', page)
  const r = await axios.post(`${BASE}/supprimer-page`, fd, { responseType: 'blob' })
  dl(r.data, 'modifie.pdf')
}

export const ajouterMotDePasse = async (pdf, mdp) => {
  const fd = new FormData()
  fd.append('pdf', pdf); fd.append('mdp', mdp)
  const r = await axios.post(`${BASE}/mot-de-passe`, fd, { responseType: 'blob' })
  dl(r.data, 'protege.pdf')
}

export const convertirImage = async (pdf, page) => {
  const fd = new FormData()
  fd.append('pdf', pdf); fd.append('page', page)
  const r = await axios.post(`${BASE}/convertir-image`, fd, { responseType: 'blob' })
  dl(r.data, `page_${page}.png`)
}

export const extraireTexte = async (pdf) => {
  const fd = new FormData()
  fd.append('pdf', pdf)
  const r = await axios.post(`${BASE}/extraire-texte`, fd)
  return r.data
}

export const creerPDF = async (contenu) => {
  const fd = new FormData()
  fd.append('contenu', contenu)
  const r = await axios.post(`${BASE}/creer`, fd, { responseType: 'blob' })
  dl(r.data, 'nouveau.pdf')
}
