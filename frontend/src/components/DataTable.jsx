import { Eye, Pencil, Trash2 } from 'lucide-react'

function DataTable({ columns, data, emptyMessage, onView, onEdit, onDelete }) {
  return (
    <div className="table-wrap">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.label}</th>
            ))}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td colSpan={columns.length + 1} className="empty-row">
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((item) => (
              <tr key={item.id}>
                {columns.map((column) => (
                  <td key={column.key}>
                    {column.render ? column.render(item) : item[column.key]}
                  </td>
                ))}
                <td>
                  <div className="row-actions">
                    {onView && (
                      <button type="button" className="icon-action view" onClick={() => onView(item)} aria-label="View">
                        <Eye size={16} />
                      </button>
                    )}
                    {onEdit && (
                      <button type="button" className="icon-action edit" onClick={() => onEdit(item)} aria-label="Edit">
                        <Pencil size={16} />
                      </button>
                    )}
                    {onDelete && (
                      <button type="button" className="icon-action delete" onClick={() => onDelete(item)} aria-label="Delete">
                        <Trash2 size={16} />
                      </button>
                    )}
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}

export default DataTable
