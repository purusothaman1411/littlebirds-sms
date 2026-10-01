/** PASS / FAIL / INCOMPLETE as a coloured label. */
export default function ResultBadge({ result }) {
  const kind = (result || '').toLowerCase();
  return <span className={`badge badge-${kind}`}>{result}</span>;
}
